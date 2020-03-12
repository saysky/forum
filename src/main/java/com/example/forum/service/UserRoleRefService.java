package com.example.forum.service;

import com.example.forum.entity.UserRoleRef;
import com.example.forum.common.base.BaseService;


public interface UserRoleRefService extends BaseService<UserRoleRef, Long> {

    /**
     * 根据用户Id删除
     *
     * @param userId 用户Id
     */
    void deleteByUserId(Long userId);


}
