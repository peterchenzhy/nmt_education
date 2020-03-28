package com.nmt.education.commmons;

import lombok.Getter;


/**
 * 枚举类集合
 *
 * @Author: PeterChen
 * @Date: 2020/3/21 23:43
 * @Version 1.0
 */
public interface Enums {

    /**
     * 课程状态
     */
    enum CourseStatus implements IEnum {
        未开课(0, "未开课"),
        已开学(1, "已开学"),
        已结课(2, "已结课"),
        已取消(3, "已取消");

        @Getter
        private Integer code;
        @Getter
        private String desc;

        CourseStatus(Integer code, String desc) {
            this.desc = desc;
            this.code = code;
        }
    }

    /**
     * 费用状态
     */
    enum FeeStatus implements IEnum {
        未缴费(0, "未缴费"),
        已缴费(1, "已缴费"),
        已冻结(2, "已冻结"),
        已退费(3, "已退费");

        @Getter
        private Integer code;
        @Getter
        private String desc;

        FeeStatus(Integer code, String desc) {
            this.desc = desc;
            this.code = code;
        }
    }


    /**
     * 报名状态
     */
    enum registrationStatus implements IEnum {
        新报(1, "新报"),
        续报(2, "续报"),
        退费中(3, "退费中"),
        已退费(4, "已退费");

        @Getter
        private Integer code;
        @Getter
        private String desc;

        registrationStatus(Integer code, String desc) {
            this.desc = desc;
            this.code = code;
        }
    }
}
