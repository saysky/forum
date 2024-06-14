package com.example.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.forum.common.base.BaseEntity;
import com.example.forum.util.RelativeDateFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author example
 */
@Data
@TableName("post")
public class Post extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 帖子标题
     */
    private String postType;

    /**
     * 帖子标题
     */
    private String postTitle;

    /**
     * 帖子内容 html格式
     */
    private String postContent;

    /**
     * 帖子摘要
     */
    private String postSummary;

    /**
     * 缩略图
     */
    private String postThumbnail;

    /**
     * 0 已发布
     * 1 草稿
     * 2 回收站
     */
    private Integer postStatus;

    /**
     * 帖子访问量
     */
    private Long postViews;

    /**
     * 点赞访问量
     */
    private Long postLikes;

    /**
     * 回帖数量(冗余字段，加快查询速度)
     */
    private Long commentSize;


    /**
     * 发表用户 多对一
     */
    @TableField(exist = false)
    private User user;

    /**
     * 帖子所属分类
     */
    @TableField(exist = false)
    private Category category;

    /**
     * 帖子所属标签
     */
    @TableField(exist = false)
    private List<Tag> tagList = new ArrayList<>();

    /**
     * 帖子的回帖
     */
    @TableField(exist = false)
    private List<Comment> comments = new ArrayList<>();

    /**
     * 更新时间
     */
    @TableField(exist = false)
    private String createTimeStr;

    public String getCreateTimeStr() {
        return RelativeDateFormat.format(getCreateTime());
    }

