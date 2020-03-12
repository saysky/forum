package com.example.forum.service;


import com.example.forum.common.base.BaseService;
import com.example.forum.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 *     回帖业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/22
 */
public interface CommentService extends BaseService<Comment, Long> {

    /**
     * 根据用户Id删除回帖
     *
     * @param userId 用户Id
     */
    Integer deleteByUserId(Long userId);

    /**
     * 根据回帖接受人Id删除回帖
     *
     * @param acceptId 用户Id
     */
    Integer deleteByAcceptUserId(Long acceptId);

    /**
     * 根据文章ID获得评论列表
     * @param postId
     * @return
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 根据用户ID，标记所有收到的回帖为已读
     * @param userId
     */
    void readAllByUserId(Long userId);

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
