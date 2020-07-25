package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FeeStatisticsVo {

    @ApiModelProperty(value = "订单编号")
    private String registrationNumber;

    @ApiModelProperty(value = "课程id")
    private Long courseId;

    @ApiModelProperty(value = "学生id")
    private Long studentId;

    @ApiModelProperty(value = "课程名字")
    private String courseName;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "费用时间")
    private Date feeTime;

    @ApiModelProperty(value = "费用金额")
    private String amount;

    /**
     * @see com.nmt.education.commmons.ExpenseDetailFlowTypeEnum
     */
    @ApiModelProperty(value = "费用类型 1：缴费 2：退费")
    private Integer feeFlowType;

    @ApiModelProperty(value = "费用类型")
    private String feeFlowTypeStr;

    @ApiModelProperty(value = "所在校区")
    private int campus;
}
