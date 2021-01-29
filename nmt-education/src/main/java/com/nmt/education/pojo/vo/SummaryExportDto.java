package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringNumberConverter;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.NumberUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

//消耗汇总表
@Getter
@Setter
public class SummaryExportDto {

    //基本信息
    @ExcelProperty(value = "课程名字", index = 0)
    private String courseName;

    @ExcelProperty(value = "课程状态", index = 1)
    private String courseStatusStr;

    @ExcelProperty(value = "科目", index = 2)
    private String subject;

    @ExcelProperty(value = "学生姓名", index = 3)
    private String studentName;


    @ExcelProperty(value = "实际出勤", index = 4)
    private Long actuallyAttendance;

    @ExcelProperty(value = "实际缺勤", index = 5)
    private Long actuallyAbsence;

    @ExcelProperty(value = "实报课次", index = 6)
    private Integer actuallyApplyAttendance;


    //报名次数×单价×折扣系数+讲义费
    @ExcelProperty(value = "实收学费", index = 7, converter = StringNumberConverter.class)
    private String actuallyTotalExpense;

    //课程设计课次
    @ExcelProperty(value = "总课次", index = 8)
    private Integer totalTimes;

    //应报考勤×单价×折扣系数+讲义费
    @ExcelProperty(value = "应收学费", index = 9, converter = StringNumberConverter.class)
    private String shouldApplyTotalExpense;

    //应收学费-实收学费
    @ExcelProperty(value = "应报实报差额", index = 10, converter = StringNumberConverter.class)
    private String totalExpenseDiff;

    //原价 单节价格
    @ExcelProperty(value = "单价（节课）", index = 11, converter = StringNumberConverter.class)
    private String perPrice;

    @ExcelProperty(value = "讲义费", index = 12, converter = StringNumberConverter.class)
    private String bookFee = Consts.ZERO;

    @ExcelProperty(value = "折扣系数", index = 13, converter = StringNumberConverter.class)
    private String discount;

    @ExcelProperty(value = "实际单价（节课）", index = 14, converter = StringNumberConverter.class)
    private String actuallyPerPrice;

    @ExcelProperty(value = "实际讲义费", index = 15, converter = StringNumberConverter.class)
    private String actuallyBookFee = Consts.ZERO;

    //实际出勤×单价×折扣系数+讲义费
    @ExcelProperty(value = "实际消耗", index = 16, converter = StringNumberConverter.class)
    private String actuallyConsume;

    //实收学费-实际消耗
    @ExcelProperty(value = "未消耗", index = 17, converter = StringNumberConverter.class)
    private String unexpired;

    @ExcelProperty(value = "报名备注", index = 18)
    private String registerRemark;

    @ExcelProperty(value = "退费金额", index = 19, converter = StringNumberConverter.class)
    private String refund;

    @ExcelProperty(value = "结余抵扣", index = 20, converter = StringNumberConverter.class)
    private String surplusDeduction;


    public SummaryExportDto calcFields() {
        //实际消耗
        this.actuallyConsume = (new BigDecimal(this.actuallyAttendance)
                .multiply(NumberUtil.String2Dec(this.discount))
                .multiply(NumberUtil.String2Dec(this.actuallyPerPrice)))
                .add(NumberUtil.String2Dec(this.actuallyBookFee))
                .toPlainString();
        //未消耗
        this.unexpired = new BigDecimal(this.actuallyAbsence)
                .multiply(NumberUtil.String2Dec(this.discount))
                .multiply(NumberUtil.String2Dec(this.actuallyPerPrice))
                .toPlainString();
        //实收学费
        this.actuallyTotalExpense = (new BigDecimal(this.actuallyApplyAttendance)
                .multiply(NumberUtil.String2Dec(this.discount))
                .multiply(NumberUtil.String2Dec(this.actuallyPerPrice)))
                .add(NumberUtil.String2Dec(actuallyBookFee))
                .toPlainString();
        //实付应付差额
        this.totalExpenseDiff = NumberUtil.String2Dec(this.shouldApplyTotalExpense)
                .subtract(NumberUtil.String2Dec(this.actuallyTotalExpense))
                .toPlainString();

        return this;
    }

}
