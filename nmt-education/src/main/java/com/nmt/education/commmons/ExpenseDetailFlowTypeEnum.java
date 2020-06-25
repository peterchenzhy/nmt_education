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
public enum ExpenseDetailFlowTypeEnum {
    新增记录(1, "新增记录"),
    编辑(2, "编辑"),
    消耗(3, "消耗"),
    还原(4, "还原"),
    退费(5, "退费"),
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
    public static ExpenseDetailFlowTypeEnum codeOf(Integer code) {
        for (ExpenseDetailFlowTypeEnum e : ExpenseDetailFlowTypeEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        throw new IllegalArgumentException("StatusEnum code 参数非法，找不到对应的枚举,code:" + code);
    }

    ExpenseDetailFlowTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}


