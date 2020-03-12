package com.example.forum.controller.admin;

import com.example.forum.common.constant.CommonConstant;
import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.User;
import com.example.forum.dto.JsonResult;
import com.example.forum.service.RoleService;
import com.example.forum.service.UserService;
import com.example.forum.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 后台用户管理控制器
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/user")
public class ProfileController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 获取用户信息并跳转
     *
     * @return 模板路径admin/admin_profile
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        //1.用户信息
        User user = getLoginUser();
        model.addAttribute("user", user);
        return "admin/admin_profile";
    }


    /**
     * 处理修改用户资料的请求
     *
     * @param user user
     * @return JsonResult
     */
    @PostMapping(value = "/profile/save")
    @ResponseBody
    public JsonResult saveProfile(@ModelAttribute User user) {
        User loginUser = getLoginUser();

        User saveUser = userService.get(loginUser.getId());
        saveUser.setUserPass(null);
        saveUser.setId(loginUser.getId());
        saveUser.setUserName(user.getUserName());
        saveUser.setUserDisplayName(user.getUserDisplayName());
        saveUser.setUserAvatar(user.getUserAvatar());
        saveUser.setUserDesc(user.getUserDesc());
        saveUser.setUserEmail(user.getUserEmail());
        userService.insertOrUpdate(saveUser);
        return JsonResult.success("资料修改成功，请重新登录");
    }


    /**
     * 处理修改密码的请求
     *
     * @param beforePass 旧密码
     * @param newPass    新密码
     * @return JsonResult
     */
    @PostMapping(value = "/changePass")
    @ResponseBody
    public JsonResult changePass(@ModelAttribute("beforePass") String beforePass,
                                 @ModelAttribute("newPass") String newPass) {
        User loginUser = getLoginUser();
        User user = userService.get(loginUser.getId());
        if (user != null && Objects.equals(user.getUserPass(), Md5Util.toMd5(beforePass, CommonConstant.PASSWORD_SALT, 10))) {
            userService.updatePassword(user.getId(), newPass);
        } else {
            return JsonResult.error("旧密码错误");
        }
        return JsonResult.success("密码重置成功");
    }


}
