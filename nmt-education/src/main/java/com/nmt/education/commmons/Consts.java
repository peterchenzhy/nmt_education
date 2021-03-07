package com.nmt.education.commmons;


/**
 * 常量
 *
 * @Author: PeterChen
 * @Date: 2020/3/19 0:05
 * @Version 1.0
 */
public interface Consts {

    String LOGIN_USER_HEAD = "loginUser";
    String ROLE_ID_HEAD = "roleId";
    String NMT_TOKEN_HEAD = "token";
    String ROLE_ROOT ="root";

    int SYSTEM_USER = 0;
    long DEFAULT_LONG = -1l;

    String 分号 = ";";

    String X校区 = "XXQ";
    int 学生编号长度 = 15;
    int 课程编号长度 = 20;
    int 订单编号长度 = 20;
    String 订单前缀 = "CR";

    Integer FEE_TYPE_普通单节费用 = 1;
    Integer FEE_TYPE_费用类型 = 7;
    Integer CONFIG_TYPE_校区 = 1;
    Integer CONFIG_TYPE_科目 = 4;
    Integer CONFIG_TYPE_年级 = 6;
    Integer CONFIG_TYPE_季节 = 2;

    int BATCH_100 = 100;
    int BATCH_10 = 10;


    String ZERO = "0";
    String 结余消耗模板="课程：[%s]，科目：[%s]，消耗结余金额：[%s]元；";
    String 新增结余账户模板="新增结余账户，来源：[%s]";
    String 账户金额更新模板="账户金额编辑,备注：";


    String 结转进学生账户REMARK = "[%s]课程结转增加金额:[%s]元";
    String 退费进学生账户REMARK = "[%s]课程退费增加金额:[%s]元";

}

