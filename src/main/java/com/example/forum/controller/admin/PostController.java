package com.example.forum.controller.admin;

import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.exception.MyBusinessException;
import com.example.forum.entity.*;
import com.example.forum.enums.*;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import com.example.forum.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     后台文章管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;


    public static final String TITLE = "title";

    public static final String CONTENT = "content";


    /**
     * 处理后台获取文章列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "status", defaultValue = "0") Integer status,
                        @RequestParam(value = "keywords", defaultValue = "") String keywords,
                        @RequestParam(value = "searchType", defaultValue = "") String searchType,
                        @RequestParam(value = "postSource", defaultValue = "-1") Integer postSource,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @ModelAttribute SearchVo searchVo) {

        Long loginUserId = getLoginUserId();
        Post condition = new Post();
        if (!StringUtils.isBlank(keywords)) {
            if (TITLE.equals(searchType)) {
                condition.setPostTitle(keywords);
            } else {
                condition.setPostContent(keywords);
            }
        }
        condition.setPostType(PostTypeEnum.POST_TYPE_POST.getValue());
        condition.setPostStatus(status);
        // 管理员可以查看所有用户的，非管理员只能看到自己的帖子
        if(!loginUserIsAdmin()) {
            condition.setUserId(loginUserId);
        }

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> posts = postService.findAll(
                page,
                new QueryCondition<>(condition, searchVo));

        //封装分类和标签
        model.addAttribute("posts", posts.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("status", status);
        model.addAttribute("keywords", keywords);
        model.addAttribute("searchType", searchType);
        model.addAttribute("postSource", postSource);
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);
        return "admin/admin_post";
    }


    /**
     * 处理跳转到新建文章页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/new")
    public String newPost(Model model) {
        //所有分类
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("categories", allCategories);
        return "admin/admin_post_new";
    }



    /**
     * 添加/更新文章
     *
     * @param post    Post实体
     * @param cateId 分类ID
     * @param tags 标签列表
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushPost(@ModelAttribute Post post,
                               @RequestParam("cateId") Long cateId,
                               @RequestParam("tags") String tags) {

        // 1，检查分类和标签
        checkTags(tags);

        // 2.获得登录用户
        User user = getLoginUser();
        Boolean isAdmin = loginUserIsAdmin();
        post.setUserId(getLoginUserId());

        //3、非管理员只能修改自己的文章，管理员都可以修改
        Post originPost = null;
        if (post.getId() != null) {
            originPost = postService.get(post.getId());
            if (!Objects.equals(originPost.getUserId(), user.getId()) && !isAdmin) {
                return JsonResult.error("没有权限");
            }
            //以下属性不能修改
            post.setUserId(originPost.getUserId());
            post.setPostViews(originPost.getPostViews());
            post.setCommentSize(originPost.getCommentSize());
            post.setPostLikes(originPost.getPostLikes());
            post.setCommentSize(originPost.getCommentSize());
            post.setDelFlag(originPost.getDelFlag());
        }
        // 4、提取摘要
        int postSummary = 100;
        //文章摘要
        String summaryText = HtmlUtil.cleanHtmlTag(post.getPostContent());
        if (summaryText.length() > postSummary) {
            String summary = summaryText.substring(0, postSummary);
            post.setPostSummary(summary);
        } else {
            post.setPostSummary(summaryText);
        }

        // 5、分类标签
        Category category = new Category();
        category.setId(cateId);
        post.setCategory(category);
        if (StringUtils.isNotEmpty(tags)) {
            List<Tag> tagList = tagService.strListToTagList(StringUtils.deleteWhitespace(tags));
            post.setTagList(tagList);
        }

        // 6.类型
        post.setPostType(PostTypeEnum.POST_TYPE_POST.getValue());

        // 7.添加/更新入库
        postService.insertOrUpdate(post);
        return JsonResult.success("发布成功");
    }

    /**
     * 限制一篇文章最多5个标签
     *
     * @param tagList
     */
    private void checkTags(String tagList) {
        String[] tags = tagList.split(",");
        if (tags.length > 5) {
            throw new MyBusinessException("每篇文章最多5个标签");
        }
        for (String tag : tags) {
            if (tag.length() > 20) {
                throw new MyBusinessException("每个标签长度最多为20个字符");
            }
        }
    }

    /**
     * 处理移至回收站的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/throw")
    @ResponseBody
    public JsonResult moveToTrash(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        post.setPostStatus(PostStatusEnum.RECYCLE.getCode());
        postService.update(post);
        return JsonResult.success("操作成功");

    }

    /**
     * 处理文章为发布的状态
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/revert")
    @ResponseBody
    public JsonResult moveToPublish(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        post.setPostStatus(PostStatusEnum.PUBLISHED.getCode());
        postService.update(post);
        return JsonResult.success("操作成功");
    }



    /**
     * 处理删除文章的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult removePost(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        postService.delete(postId);
        return JsonResult.success("删除成功");
    }

    /**
     * 批量删除
     *
     * @param ids 文章ID列表
     * @return 重定向到/admin/post
     */
    @DeleteMapping(value = "/batchDelete")
    @ResponseBody
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        Long userId = getLoginUserId();
        //批量操作
        //1、防止恶意操作
        if (ids == null || ids.size() == 0 || ids.size() >= 100) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "参数不合法!");
        }
        //2、检查用户权限
        //文章作者才可以删除
        List<Post> postList = postService.findByBatchIds(ids);
        for (Post post : postList) {
            if (!Objects.equals(post.getUserId(), userId) && !loginUserIsAdmin()) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), "没有权限");
            }
        }
        //3、如果当前状态为回收站，则删除；否则，移到回收站
        for (Post post : postList) {
            if (Objects.equals(post.getPostStatus(), PostStatusEnum.RECYCLE.getCode())) {
                postService.delete(post.getId());
            } else {
                post.setPostStatus(PostStatusEnum.RECYCLE.getCode());
                postService.update(post);
            }
        }
        return JsonResult.success("删除成功");
    }


    /**
     * 检查文章是否存在和用户是否有权限控制
     *
     * @param post
     */
    private void basicCheck(Post post) {
        if (post == null) {
            throw new MyBusinessException("文章不存在");
        }
        //只有创建者有权删除
        User user = getLoginUser();
        //管理员和文章作者可以删除
        Boolean isAdmin = loginUserIsAdmin();
        if (!Objects.equals(post.getUserId(), user.getId()) && !isAdmin) {
            throw new MyBusinessException("没有权限");
        }
    }

    /**
     * 跳转到编辑文章页面
     *
     * @param postId 文章编号
     * @param model  model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editPost(@RequestParam("id") Long postId, Model model) {
        Post post = postService.get(postId);
        basicCheck(post);

        //当前文章标签
        List<Tag> tagList = tagService.findByPostId(postId);
        String tags = tagService.tagListToStr(tagList);
        model.addAttribute("tags", tags);

        //当前文章分类
        Category category = categoryService.findByPostId(postId);
        post.setCategory(category);
        model.addAttribute("post", post);

        //所有分类
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("categories", allCategories);
        return "admin/admin_post_edit";
    }


}
