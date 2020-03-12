package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.PostTagRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface PostTagRefMapper extends BaseMapper<PostTagRef> {

    /**
     * 根据帖子Id删除记录
     *
     * @param postId 帖子Id
     * @return 影响行数
     */
    Integer deleteByPostId(Long postId);

    /**
     * 根据标签Id删除记录
     *
     * @param tagId 标签Id
     * @return 影响行数
     */
    Integer deleteByTagId(Long tagId);

    /**
     * 根据标签Id查询帖子Id
     *
     * @param tagId 标签Id
     * @return 帖子Id列表
     */
    List<Long> selectPostIdByTagId(Long tagId);

    /**
     * 根据帖子Id查询标签Id
     * @param postId 帖子Id
     * @return 标签Id列表
     */
    List<Long> selectTagIdByPostId(Long postId);
}

