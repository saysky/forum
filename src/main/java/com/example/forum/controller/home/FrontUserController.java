package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.entity.*;
import com.example.forum.dto.QueryCondition;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 言曌
 * @date 2020/3/11 4:59 下午
 */
@Controller
public class FrontUserController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    /**
     * 用户列表
     *
     * @param model
     * @return
     */
    @GetMapping("/user")
    public String hotUser(Model model) {
        List<User> users = userService.getHotUsers(100);
        model.addAttribute("users", users);
        return "home/user";
    }

    /**
     * 用户帖子列表
     *
     * @param model
     * @return
     */
    @GetMapping("/user/{id}")
    public String index(@PathVariable("id") Long userId,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {

        User user = userService.get(userId);
        if(user == null) {
            return renderNotFound();
        }

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setUserId(userId);
        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("user", user);
        return "home/user_post";
    }


}
