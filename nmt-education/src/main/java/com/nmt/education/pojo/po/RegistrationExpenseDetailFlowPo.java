package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "com-nmt-education-pojo-po-RegistrationExpenseDetailFlow")
@Getter
@Setter
@ToString
public class RegistrationExpenseDetailFlowPo {
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
     * 流水类型1-新增2-编辑3-消耗4-还原
     */
    @ApiModelProperty(value = "流水类型1-新增2-编辑3-消耗4-还原")
    private Integer type;

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
     * 结余抵扣
     */
    @ApiModelProperty(value="结余抵扣")
    private String accountAmount;

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
     * 有效：1 无效：0
     */
    @ApiModelProperty(value = "有效：1 无效：0")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Integer creator;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更改人
     */
    @ApiModelProperty(value = "更改人")
    private Integer operator;

    /**
     * 更改时间
     */
    @ApiModelProperty(value = "更改时间")
    private Date operateTime;
}