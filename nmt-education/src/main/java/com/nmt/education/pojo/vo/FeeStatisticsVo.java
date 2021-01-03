package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.bigdecimal.BigDecimalStringConverter;
import com.alibaba.excel.converters.string.StringNumberConverter;
import io.swagger.annotations.ApiModelProperty;
import javafx.util.converter.NumberStringConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FeeStatisticsVo {

    @ApiModelProperty(value = "订单编号")
    @ExcelProperty(value = "订单编号",index = 0)
    private String registrationNumber;

    @ApiModelProperty(value = "课程id")
    @ExcelIgnore
    private Long courseId;

    @ApiModelProperty(value = "学生id")
    @ExcelIgnore
    private Long studentId;

    @ApiModelProperty(value = "课程名字")
    @ExcelProperty(value = "课程名字",index = 1)
    private String courseName;

    @ApiModelProperty(value = "学生姓名")
    @ExcelProperty(value = "学生姓名",index = 2)
    private String studentName;

    @ApiModelProperty(value = "费用时间")
    @ExcelProperty(value = "缴费时间",index = 3)
    private Date feeTime;

    @ApiModelProperty(value = "费用金额")
    @ExcelProperty(value = "费用金额",index = 4,converter = StringNumberConverter.class )
    private String amount;

    /**
     * @see com.nmt.education.commmons.ExpenseDetailFlowTypeEnum
     */
    @ApiModelProperty(value = "费用类型 1：缴费 2：退费")
    @ExcelIgnore
    private Integer feeFlowType;

    @ApiModelProperty(value = "费用类型")
    @ExcelProperty(value = "费用类型",index = 5)
    private String feeFlowTypeStr;

    @ApiModelProperty(value = "支付方式--中文")
    @ExcelProperty(value = "支付方式",index = 6)
    private String paymentStr;

    @ApiModelProperty(value = "支付方式")
    @ExcelIgnore
    private Integer payment;

    @ApiModelProperty(value = "所在校区")
    @ExcelIgnore
    private int campus;

    @ApiModelProperty(value = "所在校区--中文")
    @ExcelProperty(value = "所在校区",index = 7)
    private String campusStr;


    @ApiModelProperty(value = "备注")
    @ExcelProperty(value = "备注",index = 8)
    private String remark;

    @ApiModelProperty(value = "操作人")
    @ExcelProperty(value = "操作人",index = 9)
    private String userName;


    @ApiModelProperty(value = "操作人code")
    @ExcelIgnore
    private Integer userCode;
}
