package com.nmt.education.commmons;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 角色 状态枚举
 *
 * @Author: PeterChen
 * @Date: 2019/7/8 15:19
 * @Version 1.0
 */
public enum RoleIdEnum {
    员工(11, "员工", "员工"),
    财务(81, "财务", "财务"),
    校长(91, "校长", "校长"),
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
     * 显示值
     */
    @Getter
    private String display;

    /**
     * 根据code生成枚举
     *
     * @param code 枚举编码
     * @return 枚举
     * @author PeterChen
     * @version v1
     * @since 2019/6/13 17:27
     */
    public static RoleIdEnum codeOf(Integer code) {
        for (RoleIdEnum e : RoleIdEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        throw new IllegalArgumentException("RoleIdEnum code 参数非法，找不到对应的枚举,code:" + code);
    }

    public static String code2Display(Integer code) {
        for (RoleIdEnum e : RoleIdEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e.getDisplay();
            }
        }
        return "";
    }


    RoleIdEnum(int code, String description, String display) {
        this.code = code;
        this.description = description;
        this.display = display;
    }
}


