package com.nmt.education.commmons;

import lombok.Getter;

import java.util.Objects;

/**
 * 状态枚举
 *
 * @Author: PeterChen
 * @Date: 2019/7/8 15:19
 * @Version 1.0
 */
public enum StatusEnum {
    DELETED(-1, "已删除"),
    INVALID(0, "无效"),
    VALID(1, "有效");


    /**
     * 编号
     */
    @Getter
    private int code;

    /**
     * 描述
     */
    @Getter
    private String description;

    /**
     * 根据code生成枚举
     *
     * @param code 枚举编码
     * @return 枚举
     * @author PeterChen
     * @version v1
     * @since 2019/6/13 17:27
     */
    public static StatusEnum codeOf(Integer code) {
        for (StatusEnum e : StatusEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        throw new IllegalArgumentException("StatusEnum code 参数非法，找不到对应的枚举,code:" + code);
    }

    StatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}


