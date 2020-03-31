package com.nmt.education.commmons;

import lombok.Getter;

/**
 * 数据库热配置枚举名称
 *
 * @Author: PeterChen
 * @Date: 2020/3/21 23:43
 * @Version 1.0
 */
public enum SysConfigEnum {
    校区(1, "campus"),
    季节(2, "season"),
    课程类型(3, "courseType"),
    课程科目(4, "courseSubject"),
    课程归类(5, "courseClassification"),
    年级(6, "grade"),
    费用类型(7, "feeType");


    @Getter
    private Integer code;
    @Getter
    private String desc;

    SysConfigEnum(Integer code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public static SysConfigEnum codeOf(Integer code) {
        for (SysConfigEnum e : SysConfigEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

}
