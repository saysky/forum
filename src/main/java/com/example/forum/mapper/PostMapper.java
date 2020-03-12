package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 获取所有文章阅读量总和
     *
     * @return Long
     */
    Long getPostViewsSum();

    /**
     * 重置回帖数量
     *
     * @return 数量
     */
    Integer resetCommentSize(Long postId);

    /**
     * 根据用户Id删除
     *
     * @return 影响行数
     */
    Integer deleteByUserId(Long userId);

    /**
     * 文章点赞量+1
     *
     * @param postId
     * @return
     */
    Integer incrPostLikes(Long postId);

    /**
     * 文章访问量+1
     *
     * @param postId
     * @return
     */
    Integer incrPostViews(Long postId);


    /**
     * 获得今日新增数量
     *
     * @return
     */
    Integer getTodayCount();

    /**
     * 根据标签ID查询文章
     *
     * @param condition
     * @param page
     * @return
     */
    List<Post> findPostByCondition(@Param("condition") PostQueryCondition condition, Page page);


}

