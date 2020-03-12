package com.example.forum.enums;

/**
 * <pre>
 *     角色类型enum
 * </pre>
 *
 */
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN("admin"),

    /**
     * 普通用户
     */
    USER("user"),

    /**
     * 驾校负责人
     */
    DRIVING_SCHOOL_USER("driving_school_user");

    private String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
