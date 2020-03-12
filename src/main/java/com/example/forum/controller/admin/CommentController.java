package com.example.forum.controller.admin;

import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.Comment;
import com.example.forum.entity.Post;
import com.example.forum.entity.User;
import com.example.forum.exception.MyBusinessException;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.enums.*;
import com.example.forum.service.CommentService;
import com.example.forum.service.PostService;
import com.example.forum.service.UserService;
import com.example.forum.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     后台回帖管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/comment")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;


    /**
     * 渲染回帖管理页面
     *
     * @param model      model
     * @param pageNumber page 当前页码
     * @param pageSize   size 每页显示条数
     * @return 模板路径admin/admin_comment
     */
    @GetMapping
    public String comments(Model model,
                           @RequestParam(value = "keywords", defaultValue = "") String keywords,
                           @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                           @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                           @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                           @RequestParam(value = "order", defaultValue = "desc") String order) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Comment condition = new Comment();
//        condition.setAcceptUserId(loginUserId);
        condition.setCommentContent(keywords);
        Page<Comment> comments = commentService.findAll(page, new QueryCondition<>(condition));
        List<Comment> commentList = comments.getRecords();
        commentList.forEach(comment -> comment.setPost(postService.get(comment.getPostId())));
        commentList.forEach(comment -> comment.setUser(userService.get(comment.getUserId())));
        model.addAttribute("comments", commentList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("keywords", keywords);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "admin/admin_comment";
    }

    /**
     * 我发送的回帖
     *
     * @param model      model
     * @param pageNumber page 当前页码
     * @param pageSize   size 每页显示条数
     * @return 模板路径admin/admin_comment
     */
    @GetMapping("/send")
    public String sendComments(Model model,
                               @RequestParam(value = "keywords", defaultValue = "") String keywords,
                               @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                               @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                               @RequestParam(value = "order", defaultValue = "desc") String order) {
        User user = getLoginUser();
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Comment condition = new Comment();
        condition.setUserId(user.getId());
        condition.setCommentContent(keywords);
        Page<Comment> comments = commentService.findAll(page, new QueryCondition<>(condition));
        List<Comment> commentList = comments.getRecords();
        commentList.forEach(comment -> comment.setPost(postService.get(comment.getPostId())));
        commentList.forEach(comment -> comment.setUser(userService.get(comment.getUserId())));
        model.addAttribute("comments", commentList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("keywords", keywords);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "admin/admin_comment";
    }

    /**
     * 我发送的回帖
     *
     * @param model      model
     * @param pageNumber page 当前页码
     * @param pageSize   size 每页显示条数
     * @return 模板路径admin/admin_comment
     */
    @GetMapping("/receive")
    public String receiveComments(Model model,
                               @RequestParam(value = "keywords", defaultValue = "") String keywords,
                               @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                               @RequestParam(value = "sort", defaultValue = "is_read") String sort,
                               @RequestParam(value = "order", defaultValue = "asc") String order) {
        User user = getLoginUser();
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Comment condition = new Comment();
        condition.setAcceptUserId(user.getId());
        condition.setCommentContent(keywords);
        Page<Comment> comments = commentService.findAll(page, new QueryCondition<>(condition));
        List<Comment> commentList = comments.getRecords();
        commentList.forEach(comment -> comment.setPost(postService.get(comment.getPostId())));
        commentList.forEach(comment -> comment.setUser(userService.get(comment.getUserId())));
        model.addAttribute("comments", commentList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("keywords", keywords);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "admin/admin_comment_receive";
    }

    /**
     * 删除回帖
     *
     * @param commentId commentId
     * @return string 重定向到/admin/comment
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult moveToAway(@RequestParam("id") Long commentId) {
        //回帖
        Comment comment = commentService.get(commentId);
        //检查权限
        basicCheck(comment);

        commentService.delete(commentId);
        return JsonResult.success("删除成功");
    }


    /**
     * 回复回帖，并通过回帖
     *
     * @param commentId      被回复的回帖
     * @param commentContent 回复的内容
     * @return 重定向到/admin/comment
     */
    @PostMapping(value = "/reply")
    @ResponseBody
    public JsonResult replyComment(@RequestParam("id") Long commentId,
                                   @RequestParam("commentContent") String commentContent) {
        //博主信息
        User loginUser = getLoginUser();
        //被回复的回帖
        Comment lastComment = commentService.get(commentId);
        User user = userService.get(lastComment.getUserId());
        String at = user != null ? user.getUserDisplayName() : "楼上";
        if (lastComment == null) {
            return JsonResult.error("回帖不存在");
        }

        Post post = postService.get(lastComment.getPostId());
        if (post == null) {
            return JsonResult.error("帖子不存在");
        }

        //保存回帖
        Comment comment = new Comment();
        comment.setUserId(loginUser.getId());
        comment.setPostId(lastComment.getPostId());
        String lastContent = "<a href='#comment-id-" + lastComment.getId() + "'>@" + at + "</a> ";
        comment.setCommentContent(lastContent + HtmlUtil.escape(commentContent));
        comment.setCommentParent(commentId);
        comment.setAcceptUserId(lastComment.getUserId());
        comment.setPathTrace(lastComment.getPathTrace() + lastComment.getId() + "/");
        commentService.insertOrUpdate(comment);
        return JsonResult.success("回复成功");

    }

    /**
     * 批量删除
     *
     * @param ids 回帖ID列表
     * @return
     */
    @DeleteMapping(value = "/batchDelete")
    @ResponseBody
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        //批量操作
        //1、防止恶意操作
        if (ids == null || ids.size() == 0 || ids.size() >= 100) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "参数不合法!");
        }
        //2、检查用户权限
        //文章作者、回帖人、管理员才可以删除
        List<Comment> commentList = commentService.findByBatchIds(ids);
        for (Comment comment : commentList) {
            basicCheck(comment);
        }
        //3、删除
        commentService.batchDelete(ids);
        return JsonResult.success("删除成功");
    }

    /**
     * 全部标记为已读
     *
     * @return
     */
    @PostMapping(value = "/readAll")
    @ResponseBody
    public JsonResult readAll() {
        User user = getLoginUser();
        commentService.readAllByUserId(user.getId());
        return JsonResult.success("标记成功");
    }

    /**
     * 检查文章是否存在和用户是否有权限控制
     *
     * @param comment
     */
    private void basicCheck(Comment comment) {
        Long loginUserId = getLoginUserId();
        if (comment == null) {
            throw new MyBusinessException("回帖不存在");
        }
        //文章
        Post post = postService.get(comment.getPostId());
        if (post == null) {
            throw new MyBusinessException("回帖所在文章不存在");
        }
        //检查权限，文章的作者和收到回帖和管理员的可以删除
        if (!Objects.equals(post.getUserId(), loginUserId) && !Objects.equals(comment.getAcceptUserId(), loginUserId) && !loginUserIsAdmin()) {
            throw new MyBusinessException("没有权限");
        }
    }

}
