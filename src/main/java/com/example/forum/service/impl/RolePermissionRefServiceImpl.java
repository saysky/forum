package com.example.forum.service.impl;

import com.example.forum.entity.RolePermissionRef;
import com.example.forum.mapper.RolePermissionRefMapper;
import com.example.forum.service.RolePermissionRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionRefServiceImpl implements RolePermissionRefService {

    @Autowired
    private RolePermissionRefMapper rolePermissionRefMapper;

    @Override
    public void deleteRefByRoleId(Long roleId) {
        rolePermissionRefMapper.deleteByRoleId(roleId);
    }

    @Override
    public void saveByRolePermissionRef(RolePermissionRef rolePermissionRef) {
        rolePermissionRefMapper.insert(rolePermissionRef);
    }

    @Override
    public void batchSaveByRolePermissionRef(List<RolePermissionRef> rolePermissionRefs) {
        rolePermissionRefMapper.batchInsert(rolePermissionRefs);
    }
}