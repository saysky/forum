package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.entity.Post;
import com.example.forum.entity.Tag;
import com.example.forum.service.CategoryService;
import com.example.forum.service.PostService;
import com.example.forum.service.TagService;
import com.example.forum.service.UserService;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 言曌
 * @date 2020/3/11 4:59 下午
 */
@Controller
public class FrontTagController extends BaseController {

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    /**
     * 分类列表
     *
     * @param model
     * @return
     */
    @GetMapping("/tag")
    public String tag(@RequestParam(value = "keywords", required = false) String keywords,
                      Model model) {
        List<Tag> tags = tagService.getHotTags(keywords, 100);
        model.addAttribute("tags", tags);
        return "home/tag";
    }

    /**
     * 标签对应的帖子列表
     *
     * @param model
     * @return
     */
    @GetMapping("/tag/{id}")
    public String index(@PathVariable("id") Long tagId,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {

        Tag tag = tagService.get(tagId);
        if (tag == null) {
            return renderNotFound();
        }
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setTagId(tagId);
        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("tag", tag);
        return "home/tag_post";
    }


}
