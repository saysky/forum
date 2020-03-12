package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 获得某篇帖子的标签列表
     *
     * @param postId 帖子Id
     * @return List
     */
    List<Tag> findByPostId(Long postId);

    /**
     * 获得所有包括统计帖子数
     *
     * @return 标签列表
     */
    List<Tag> findAllWithCount(Integer limit);

    /**
     * 查询没有用过的标签
     *
     * @return 标签列表
     */
    List<Tag> findTagNotUse();

    /**
     * 根据用户ID删除
     * @param userId
     * @return
     */
    Integer deleteByUserId(Long userId);

    /**
     * 热门标签
     * @param keywords
     * @param limit
     * @return
     */
    List<Tag> getHotTags(@Param("keywords") String keywords,
                         @Param("limit") Integer limit);

}

