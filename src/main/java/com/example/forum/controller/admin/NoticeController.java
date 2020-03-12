package com.example.forum.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.*;
import com.example.forum.enums.PostStatusEnum;
import com.example.forum.enums.PostTypeEnum;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.vo.SearchVo;
import com.example.forum.service.PostService;
import com.example.forum.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 *     后台公告管理控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/notice")
public class NoticeController extends BaseController {


    @Autowired
    private PostService postService;

    /**
     * 处理后台获取公告列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @RequestParam(value = "status", defaultValue = "0") Integer status,
                        @ModelAttribute SearchVo searchVo) {
        Post condition = new Post();
        condition.setPostStatus(status);
        condition.setPostType(PostTypeEnum.POST_TYPE_NOTICE.getValue());
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> postPage = postService.findAll(
                page,
                new QueryCondition<>(condition, searchVo)
        );
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("status", status);
        return "admin/admin_notice";
    }

    /**
     * 跳转到新建公告
     *
     * @return 模板路径admin/admin_notice_editor
     */
    @GetMapping(value = "/new")
    public String newPage() {
        return "admin/admin_notice_editor";
    }

    /**
     * 发表公告
     *
     * @param post post
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushPage(@ModelAttribute Post post) {

        //发表用户
        User loginUser = getLoginUser();
        post.setUserId(loginUser.getId());
        post.setPostType(PostTypeEnum.POST_TYPE_NOTICE.getValue());
        postService.insertOrUpdate(post);
        return JsonResult.success("保存成功");
    }

    /**
     * 跳转到修改公告
     *
     * @param postId 公告编号
     * @param model  model
     * @return admin/admin_page_editor
     */
    @GetMapping(value = "/edit")
    public String editPage(@RequestParam("id") Long postId, Model model) {
        Post post = postService.get(postId);
        model.addAttribute("post", post);
        return "admin/admin_notice_editor";
    }

    /**
     * 处理移至回收站的请求
     *
     * @param postId 公告编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/throw")
    @ResponseBody
    public JsonResult moveToTrash(@RequestParam("id") Long postId) {
        try {
            Post post = postService.get(postId);
            if (post == null) {
                return JsonResult.error("公告不存在");
            }
            post.setPostStatus(PostStatusEnum.RECYCLE.getCode());
            postService.update(post);
        } catch (Exception e) {
            log.error("删除公告到回收站失败：{}", e.getMessage());
            return JsonResult.error("操作失败");
        }
        return JsonResult.success("操作成功");
    }

    /**
     * 处理公告为发布的状态
     *
     * @param postId 公告编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/revert")
    @ResponseBody
    public JsonResult moveToPublish(@RequestParam("id") Long postId) {
        try {
            Post post = postService.get(postId);
            if (post == null) {
                return JsonResult.error("公告不存在");
            }

            post.setPostStatus(PostStatusEnum.PUBLISHED.getCode());
            postService.update(post);
        } catch (Exception e) {
            log.error("发布公告失败：{}", e.getMessage());
            return JsonResult.error("操作失败");
        }
        return JsonResult.success("操作成功");
    }

    /**
     * 处理删除公告的请求
     *
     * @param postId 公告编号
     * @return 重定向到/admin/post
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult removePost(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("公告不存在");
        }
        postService.delete(postId);
        return JsonResult.success("删除成功");
    }


}
