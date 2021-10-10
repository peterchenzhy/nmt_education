package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.bigdecimal.BigDecimalStringConverter;
import com.alibaba.excel.converters.string.StringNumberConverter;
import com.nmt.education.commmons.NumberUtil;
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
    @ExcelIgnore
    private Long registerId;

    @ApiModelProperty(value = "课程id")
    @ExcelIgnore
    private Long courseId;

    @ApiModelProperty(value = "学生id")
    @ExcelIgnore
    private Long studentId;

    @ApiModelProperty(value = "课程名字")
    @ExcelProperty(value = "课程名字",index = 1)
    private String courseName;
    @ExcelIgnore
    private Integer grade;
    @ExcelProperty(value = "年级",index = 2)
    private String gradeStr;

    @ExcelIgnore
    private Integer subject;
    @ExcelProperty(value = "科目",index = 3)
    private String subjectStr;

    @ApiModelProperty(value = "学生姓名")
    @ExcelProperty(value = "学生姓名",index = 4)
    private String studentName;


    @ApiModelProperty(value = "缴费日期")
    @ExcelProperty(value = "缴费日期",index = 5)
    private String feeTimeDate;

    @ApiModelProperty(value = "费用时间")
    @ExcelProperty(value = "缴费时间",index = 6)
    private Date feeTime;

    @ApiModelProperty(value = "费用金额")
    @ExcelProperty(value = "费用金额",index = 7,converter = StringNumberConverter.class )
    private String amount;

    @ApiModelProperty(value = "单价")
    @ExcelProperty(value = "单价",index = 8,converter = StringNumberConverter.class )
    private String perPrice;

    @ApiModelProperty(value = "报名次数")
    @ExcelProperty(value = "报名次数",index = 9,converter = StringNumberConverter.class )
    private String times;

    @ApiModelProperty(value = "折扣")
    @ExcelProperty(value = "折扣",index = 10,converter = StringNumberConverter.class )
    private String discount;


    @ApiModelProperty(value = "结余抵扣")
    @ExcelProperty(value = "结余抵扣",index = 11,converter = StringNumberConverter.class )
    private String accountAmount;

    @ApiModelProperty(value = "实际费用")
    //实际收费= 费用-结余抵扣
    @ExcelProperty(value = "实际费用",index = 12,converter = StringNumberConverter.class )
    private String actuallyAmount;
    public void  setActuallyAmount(){
       this.actuallyAmount=
               NumberUtil.String2Dec(this.getAmount())
                       .subtract(NumberUtil.String2Dec(this.getAccountAmount()))
                       .stripTrailingZeros()
                       .toPlainString();
    }

    /**
     * @see com.nmt.education.commmons.ExpenseDetailFlowTypeEnum
     */
    @ExcelIgnore
    private Integer feeFlowType;

    @ApiModelProperty(value = "费用类型")
    @ExcelProperty(value = "费用类型",index = 13)
    private String feeFlowTypeStr;

    @ApiModelProperty(value = "支付方式--中文")
    @ExcelProperty(value = "支付方式",index = 14)
    private String paymentStr;

    @ApiModelProperty(value = "支付方式")
    @ExcelIgnore
    private Integer payment;

    @ApiModelProperty(value = "所在校区")
    @ExcelIgnore
    private int campus;

    @ApiModelProperty(value = "所在校区--中文")
    @ExcelProperty(value = "所在校区",index = 15)
    private String campusStr;


    @ApiModelProperty(value = "备注")
    @ExcelProperty(value = "备注",index = 16)
    private String remark;

    @ApiModelProperty(value = "操作人")
    @ExcelProperty(value = "操作人",index = 17)
    private String userName;


    @ApiModelProperty(value = "操作人code")
    @ExcelIgnore
    private Integer userCode;
}
