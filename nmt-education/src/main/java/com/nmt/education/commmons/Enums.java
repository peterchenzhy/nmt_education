package com.nmt.education.commmons;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.Objects;


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
        正常(1, "正常", "default"),
        已冻结(2, "已冻结", "processing"),
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

    /**
     * 报名类型
     */
    enum RegistrationType implements IEnum {
        新报(1, "新报", "default"),
        续报(2, "续报", "success"),
//        试听课(3, "试听课", "success")
        ;

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        RegistrationType(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }

    /**
     * 编辑标志
     */
    enum EditFlag implements IEnum {
        无变化(0, "无变化", null),
        新增(1, "新增", null),
        修改(2, "修改", null),
        需要删除(3, "需要删除", null);

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        EditFlag(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }

        public static EditFlag codeOf(Integer code) {
            EditFlag result = null;
            for (EditFlag e : EditFlag.values()) {
                if (e.getCode().equals(code)) {
                    return e;
                }
            }
            return result;
        }
    }

    /**
     * 签到状态
     */
    enum SignInType implements IEnum {
        未签到(0, "未签到", null),
        已签到(1, "已签到", null),
        请假(2, "请假", null),
        已退费(3, "已退费", null),
//        锁定(3, "锁定", null),
        ;
        public static List<Integer> canRefund = Lists.newArrayList(未签到.code, 请假.code);


        public static Boolean isConsumed(SignInType source, SignInType target) {
            if (Objects.isNull(source) || Objects.isNull(target) || source.equals(target) || 已退费.equals(source)) {
                return null;
            }
            if (已签到.equals(target)) {
                return true;
            }
            if (已签到.equals(source)) {
                return false;
            }
            return null;
        }

        public static SignInType codeOf(Integer code) {
            SignInType result = null;
            for (SignInType e : SignInType.values()) {
                if (e.getCode().equals(code)) {
                    return e;
                }
            }
            return result;
        }

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        SignInType(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }

    /**
     * 费用方向
     */
    enum FeeDirection implements IEnum {
        支付(1, "支付", null),
        退费(2, "退费", null),
//        锁定(3, "锁定", null),
        ;

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        FeeDirection(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }

    /**
     * 支付方式
     */
    enum PaymentType implements IEnum {
        现金(1, "现金", null),
        刷卡(2, "刷卡", null),
        微信(3, "微信", null),
        支付宝(4, "支付宝", null),
//        锁定(3, "锁定", null),
        ;

        @Getter
        private Integer code;
        @Getter
        private String desc;
        @Getter
        private String icon;

        PaymentType(Integer code, String desc, String icon) {
            this.desc = desc;
            this.code = code;
            this.icon = icon;
        }
    }
}
