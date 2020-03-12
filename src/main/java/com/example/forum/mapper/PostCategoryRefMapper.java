package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.PostCategoryRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author liuyanzhao
 */
@Mapper
public interface PostCategoryRefMapper extends BaseMapper<PostCategoryRef> {

    /**
     * 根据帖子Id删除记录
     *
     * @param postId 帖子Id
     * @return 影响行数
     */
    Integer deleteByPostId(Long postId);

    /**
     * 根据分类Id删除记录
     *
     * @param cateId 分类Id
     * @return 影响行数
     */
    Integer deleteByCateId(Long cateId);

    /**
     * 根据分类Id查询帖子Id
     *
     * @param cateId 分类Id
     * @return 帖子Id列表
     */
    List<Long> selectPostIdByCateId(Long cateId);

    /**
     * 根据帖子Id查询分类Id
     *
     * @param postId 帖子Id
     * @return 分类Id列表
     */
    List<Long> selectCateIdByPostId(Long postId);

    /**
     * 统计某篇分类的帖子数
     *
     * @param cateId 分类Id
     * @return 帖子Id列表
     */
    Integer countPostByCateId(Long cateId);
}

