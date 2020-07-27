package com.nmt.education.commmons;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 状态枚举
 *
 * @Author: PeterChen
 * @Date: 2019/7/8 15:19
 * @Version 1.0
 */
public enum ExpenseDetailFlowTypeEnum {
    新增记录(1, "新增记录", "缴费"),
    编辑(2, "编辑", "缴费"),
    消耗(3, "消耗", "消耗"),
    还原(4, "还原", "还原"),
    退费(5, "退费", "退费"),
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
    public static ExpenseDetailFlowTypeEnum codeOf(Integer code) {
        for (ExpenseDetailFlowTypeEnum e : ExpenseDetailFlowTypeEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e;
            }
        }
        throw new IllegalArgumentException("ExpenseDetailFlowTypeEnum code 参数非法，找不到对应的枚举,code:" + code);
    }

    public static String code2Display(Integer code) {
        for (ExpenseDetailFlowTypeEnum e : ExpenseDetailFlowTypeEnum.values()) {
            if (Objects.equals(code, e.getCode())) {
                return e.getDisplay();
            }
        }
        return "";
    }

    public static List<Integer> feeStatistics2FlowType(Integer code) {
        List<Integer> result = new ArrayList<>();
        if (code != null) {
            //支付
            if (code == 1) {
                result.add(新增记录.code);
                result.add(编辑.code);
            } else {
                result.add(退费.code);
            }

        }else{
            result.add(新增记录.code);
            result.add(编辑.code);
            result.add(退费.code);
        }
        return result;
    }

    ExpenseDetailFlowTypeEnum(int code, String description, String display) {
        this.code = code;
        this.description = description;
        this.display = display;
    }
}


