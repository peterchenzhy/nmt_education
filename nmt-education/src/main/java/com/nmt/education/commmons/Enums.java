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
        未开课(0, "未开课", "default"),
        已开学(1, "已开学", "processing"),
        已结课(2, "已结课", "success"),
        已取消(3, "已取消", "error");

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        CourseStatus(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }

    /**
     * 费用状态
     */
    enum FeeStatus implements IEnum {
        未缴费(0, "未缴费", null),
        已缴费(1, "已缴费", null),
        已冻结(2, "已冻结", null),
        已退费(3, "已退费", null);

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        FeeStatus(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }


    /**
     * 报名状态
     */
    enum RegistrationStatus implements IEnum {
        新报(1, "新报", "default"),
        续报(2, "续报", "processing"),
        退费中(3, "退费中", "success"),
        已退费(4, "已退费", "error");

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        RegistrationStatus(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }
}
