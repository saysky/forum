package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.entity.*;
import com.example.forum.dto.QueryCondition;
import com.example.forum.enums.PostStatusEnum;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 言曌
 * @date 2020/3/9 11:00 上午
 */

@Controller
public class IndexController extends BaseController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String index(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @RequestParam(value = "keywords", required = false) String keywords,
                        Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setKeywords(keywords);
        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("posts", postPage);
        return "home/index";
    }


}
