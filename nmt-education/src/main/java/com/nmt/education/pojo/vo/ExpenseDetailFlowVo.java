package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ExpenseDetailFlowVo  {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 报名id
     */
    @ApiModelProperty(value = "报名id")
    private Long registrationId;

    /**
     * 缴费id
     */
    @ApiModelProperty(value = "缴费id")
    private Long registerExpenseDetailId;

    /**
     * 费用类型
     */
    @ApiModelProperty(value = "费用类型")
    private Integer feeType;

    /**
     * 流水类型
     */
    @ApiModelProperty(value = "流水类型 ")
    private String type;

    /**
     * 单项金额
     */
    @ApiModelProperty(value = "单项金额")
    private String perAmount;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private String amount;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer count;

    /**
     * 缴费方式
     */
    @ApiModelProperty(value = "缴费方式")
    private Integer payment;

    /**
     * 折扣
     */
    @ApiModelProperty(value = "折扣")
    private String discount;


    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;


}
