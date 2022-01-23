package com.nmt.education.commmons;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 账户流水数据来源
 *
 * @Author: PeterChen
 * @Date: 2019/7/8 15:19
 * @Version 1.0
 */
public enum AccountFlowSourceEnum {
    结转(0, "结转"),
    退费(1, "退费"),
    编辑(2, "编辑"),
    后台编辑(3, "后台编辑"),
    ;


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
    public static AccountFlowSourceEnum codeOf(Integer code) {
        for (AccountFlowSourceEnum e : AccountFlowSourceEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        throw new IllegalArgumentException("AccountFlowSourceEnum code 参数非法，找不到对应的枚举,code:" + code);
    }


    AccountFlowSourceEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}


