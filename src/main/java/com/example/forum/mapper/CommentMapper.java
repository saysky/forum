package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询前limit条回帖
     *
     * @param limit 查询数量
     * @return 回帖列表
     */
    List<Comment> findLatestCommentByLimit(Integer limit);

    /**
     * 根据用户Id删除
     *
     * @param userId 用户Id
     * @return 影响行数
     */
    Integer deleteByUserId(Long userId);

    /**
     * 根据用户Id删除
     *
     * @param userId 用户Id
     * @return 影响行数
     */
    Integer deleteByAcceptUserId(Long userId);


    /**
     * 获得子回帖Id列表
     *
     * @param pathTrace 回帖pathTrace封装
     * @return 回帖Id列表
     */
    List<Long> selectChildCommentIds(@Param("pathTrace") String pathTrace);

    /**
     * 根据用户ID，标记所有收到的回帖为已读
     * @param userId
     */
    void readAllByUserId(Long userId);

    /**
     * 根据文章ID获得评论列表
     * @param postId
     * @return
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 统计某个用户未读数量
     * @return
     */
    Integer countNotReadByUserId(Long userId);

    /**
     * 更新某个用户在某篇文章下的收到的评论为已读
     * @param postId
     * @param userId
     */
    void updateIsReadByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}

