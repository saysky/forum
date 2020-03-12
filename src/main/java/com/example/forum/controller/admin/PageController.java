package com.example.forum.controller.admin;

import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.*;
import com.example.forum.enums.PostTypeEnum;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.service.LinkService;
import com.example.forum.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     后台页面管理控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/page")
public class PageController extends BaseController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private PostService postService;

    /**
     * 页面管理页面
     *
     * @param model model
     * @return 模板路径admin/admin_page
     */
    @GetMapping
    public String pages(Model model) {
        Post post = new Post();
        post.setPostType(PostTypeEnum.POST_TYPE_PAGE.getValue());
        List<Post> posts = postService.findAll(new QueryCondition<>(post));
        model.addAttribute("pages", posts);
        return "admin/admin_page";
    }

    /**
     * 获取友情链接列表并渲染页面
     *
     * @return 模板路径admin/admin_page_link
     */
    @GetMapping(value = "/links")
    public String links() {
        return "admin/admin_page_link";
    }

    /**
     * 跳转到修改页面
     *
     * @param model  model
     * @param linkId linkId 友情链接编号
     * @return String 模板路径admin/admin_page_link
     */
    @GetMapping(value = "/links/edit")
    public String toEditLink(Model model, @RequestParam("id") Long linkId) {
        Link link = linkService.get(linkId);
        model.addAttribute("updateLink", link);
        return "admin/admin_page_link";
    }

    /**
     * 处理添加/修改友链的请求并渲染页面
     *
     * @param link Link实体
     * @return 重定向到/admin/page/links
     */
    @PostMapping(value = "/links/save")
    public String saveLink(@ModelAttribute Link link) {
        linkService.insert(link);
        return "redirect:/admin/page/links";
    }

    /**
     * 处理删除友情链接的请求并重定向
     *
     * @param linkId 友情链接编号
     * @return 重定向到/admin/page/links
     */
    @DeleteMapping(value = "/links/delete")
    public String removeLink(@RequestParam("id") Long linkId) {
        linkService.delete(linkId);
        return "redirect:/admin/page/links";
    }



    /**
     * 跳转到新建页面
     *
     * @return 模板路径admin/admin_page_editor
     */
    @GetMapping(value = "/new")
    public String newPage() {
        return "admin/admin_page_editor";
    }

    /**
     * 发表页面
     *
     * @param post post
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushPage(@ModelAttribute Post post) {
        //发表用户
        User loginUser = getLoginUser();
        post.setUserId(loginUser.getId());
        post.setPostType(PostTypeEnum.POST_TYPE_PAGE.getValue());
        postService.insertOrUpdate(post);
        return JsonResult.success("保存成功");
    }


    /**
     * 跳转到修改页面
     *
     * @param pageId 页面编号
     * @param model  model
     * @return admin/admin_page_editor
     */
    @GetMapping(value = "/edit")
    public String editPage(@RequestParam("id") Long pageId, Model model) {
        Post post = postService.get(pageId);
        model.addAttribute("post", post);
        return "admin/admin_page_editor";
    }

}
