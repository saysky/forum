package com.example.forum.controller.home;

import cn.hutool.core.lang.Validator;
import com.example.forum.common.constant.CommonConstant;
import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.Role;
import com.example.forum.entity.User;
import com.example.forum.entity.UserRoleRef;
import com.example.forum.dto.JsonResult;
import com.example.forum.enums.CommonParamsEnum;
import com.example.forum.enums.TrueFalseEnum;
import com.example.forum.enums.UserStatusEnum;
import com.example.forum.service.*;
import com.example.forum.util.Md5Util;
import com.example.forum.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Set;

@Controller
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRefService userRoleRefService;

    @Autowired
    private MailService mailService;

    /**
     * 处理跳转到登录页的请求
     *
     * @return 模板路径admin/admin_login
     */
    @GetMapping(value = "/login")
    public String login() {
        return "home/login";
    }

    /**
     * 处理跳转到登录页的请求
     *
     * @return 模板路径admin/admin_login
     */
    @GetMapping(value = "/register")
    public String register() {
        return "home/register";
    }

    /**
     * 验证登录信息
     *
     * @param userName 登录名：邮箱／用户名
     * @param userPass password 密码
     * @return JsonResult JsonResult
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public JsonResult getLogin(String userName,
                               String userPass) {

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, userPass);
        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                //登录成功，修改登录错误次数为0
                User user = (User) subject.getPrincipal();
                userService.updateUserLoginNormal(user);
                Set<String> permissionUrls = permissionService.findPermissionUrlsByUserId(user.getId());
                subject.getSession().setAttribute("permissionUrls", permissionUrls);
                return JsonResult.success("登录成功");
            }
        } catch (UnknownAccountException e) {
            log.info("UnknownAccountException -- > 账号不存在：");
            return JsonResult.error("账号不存在");
        } catch (IncorrectCredentialsException e) {
            //更新失败次数
            User user;
            if (Validator.isEmail(userName)) {
                user = userService.findByEmail(userName);
            } else {
                user = userService.findByUserName(userName);
            }
            if (user != null) {
                Integer errorCount = userService.updateUserLoginError(user);
                //超过五次禁用账户
                if (errorCount >= CommonParamsEnum.FIVE.getValue()) {
                    userService.updateUserLoginEnable(user, TrueFalseEnum.FALSE.getValue());
                }
                int times = (5 - errorCount) > 0 ? (5 - errorCount) : 0;
                return JsonResult.error("用户名或密码错误，您还有" + times + "次机会");
            }
        } catch (LockedAccountException e) {
            log.info("LockedAccountException -- > 账号被锁定");
            return JsonResult.error(e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return JsonResult.error("服务器内部错误");
    }


    /**
     * 退出登录
     *
     * @return 重定向到/login
     */
    @GetMapping(value = "/logout")
    public String logOut() {
        User loginUser = getLoginUser();

        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        log.info("用户[{}]退出登录", loginUser.getUserName());
        return "redirect:/login";
    }


    /**
     * 验证注册信息
     *
     * @param userName  用户名
     * @param userEmail 邮箱
     * @return JsonResult JsonResult
     */
    @PostMapping(value = "/register")
    @ResponseBody
    public JsonResult getRegister(@ModelAttribute("userName") String userName,
                                  @ModelAttribute("userPass") String userPass,
                                  @ModelAttribute("userEmail") String userEmail) {
        // 1.校验是否输入完整
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPass) || StringUtils.isEmpty(userEmail)) {
            return JsonResult.error("请填写完整信息");
        }
        // 2.邮箱是否合法
        if (!RegexUtil.isEmail(userEmail)) {
            return JsonResult.error("邮箱不合法");
        }
        // 3.密码长度是否合法
        if (userPass.length() > 20 || userPass.length() < 6) {
            return JsonResult.error("用户密码长度为6-20位!");
        }
        //4.创建用户
        User user = new User();
        user.setUserName(userName);
        user.setUserDisplayName(userName);
        user.setUserEmail(userEmail);
        user.setLoginEnable(TrueFalseEnum.TRUE.getValue());
        user.setLoginError(0);
        user.setUserPass(Md5Util.toMd5(userPass, CommonConstant.PASSWORD_SALT, 10));
        user.setUserAvatar("/static/images/avatar/" + RandomUtils.nextInt(1, 41) + ".jpeg");
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        userService.insert(user);

        //5.关联角色
        Role defaultRole = roleService.findDefaultRole();
        if (defaultRole != null) {
            userRoleRefService.insert(new UserRoleRef(user.getId(), defaultRole.getId()));
        }


        return JsonResult.success("注册成功");
    }


    /**
     * 处理跳转忘记密码的请求
     *
     * @return 模板路径admin/admin_login
     */
    @GetMapping(value = "/forget")
    public String forget() {
        return "home/forget";
    }

    /**
     * 处理忘记密码
     *
     * @param userName  用户名
     * @param userEmail 邮箱
     * @return JsonResult
     */
    @PostMapping(value = "/forget")
    @ResponseBody
    public JsonResult getForget(@RequestParam("userName") String userName,
                                @RequestParam("userEmail") String userEmail) {

        User user = userService.findByUserName(userName);
        if (user != null && userEmail.equalsIgnoreCase(user.getUserEmail())) {
            //验证成功，将密码由邮件方法发送给对方
            //1.修改密码
            String newPass = RandomStringUtils.randomNumeric(8);
            userService.updatePassword(user.getId(), newPass);
            //2.发送邮件
            try {
                mailService.sendMail(
                        userEmail, "重置密码", "您的密码已经重置：" + newPass);
            } catch (MessagingException e) {
                e.printStackTrace();
                return JsonResult.error("邮件发送失败，系统SMTP配置错误");
            }
        } else {
            return JsonResult.error("用户名和电子邮箱不一致");
        }
        return JsonResult.success("您的密码已经发送到您的邮箱");
    }
}
