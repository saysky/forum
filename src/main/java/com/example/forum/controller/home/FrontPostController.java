package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.entity.*;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.service.*;
import com.example.forum.util.CommentUtil;
import com.example.forum.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
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
public class FrontPostController extends BaseController {

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


    public static final String NEW = "new";
    public static final String PUBLISH = "publish";
    public static final String HOT = "hot";
    public static final String USER = "user";
    public static final String CATEGORY = "category";
    public static final String TAG = "tag";

    /**
     * 加载分页数据
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/post/list")
    @ResponseBody
    public JsonResult loadPostList(@RequestParam(value = "keywords", required = false) String keywords,
                                   @RequestParam(value = "type", defaultValue = "new") String type,
                                   @RequestParam(value = "id", required = false) Long id,
                                   @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                   @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        User loginUser = getLoginUser();
        Page<Post> postPage = null;
        Page page = PageUtil.initMpPage(pageNumber, pageSize, "createTime", "desc");
        PostQueryCondition condition = new PostQueryCondition();
        if (StringUtils.isNotBlank(keywords)) {
            condition.setKeywords(keywords);
        }
        if (NEW.equalsIgnoreCase(type)) {
            // 最新帖子
        } else if (HOT.equalsIgnoreCase(type)) {
            // 最热帖子
            page = PageUtil.initMpPage(pageNumber, pageSize, "commentSize", "desc");
        } else if (USER.equalsIgnoreCase(type)) {
            // 某个用户的
            condition.setUserId(id);
        } else if (CATEGORY.equalsIgnoreCase(type) || (TAG.equalsIgnoreCase(type))) {
            // 某个板块的帖子
            condition.setCateId(id);
        } else if (PUBLISH.equalsIgnoreCase(type)) {
            // 我发布的
            if (loginUser == null) {
                return JsonResult.error("请先登录", "notLogin");
            }
            condition.setUserId(loginUser.getId());
        }

        // 获得列表
        postPage = postService.findPostByCondition(condition, page);
        return JsonResult.success("查询成功", postPage);
    }


    /**
     * 点赞文章
     *
     * @param postId
     * @return
     */
    @PostMapping("/post/like")
    @ResponseBody
    public JsonResult likePost(@RequestParam("postId") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("帖子不存在");
        }
        post.setPostLikes(post.getPostLikes() + 1);
        postService.update(post);
        return JsonResult.success();
    }


    /**
     * 帖子详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/post/{id}")
    public String postDetails(@PathVariable("id") Long id, Model model) {
        // 帖子
        Post post = postService.get(id);
        if (post == null) {
            return renderNotFound();
        }
        model.addAttribute("post", post);

        // 作者
        User user = userService.get(post.getUserId());
        model.addAttribute("user", user);

        // 分类
        Category category = categoryService.findByPostId(id);
        model.addAttribute("category", category);

        // 标签列表
        List<Tag> tagList = tagService.findByPostId(id);
        model.addAttribute("tagList", tagList);

        // 评论列表
        List<Comment> commentList = commentService.findByPostId(id);
        model.addAttribute("commentList", CommentUtil.getComments(commentList));

        // 访问量加1
        postService.updatePostView(id);

        // 标记该文章所有的评论为已读
        User loginUser = getLoginUser();
        if(loginUser != null) {
            commentService.updateIsReadByPostIdAndUserId(id, loginUser.getId());
        }
        return "home/post";
    }


}
