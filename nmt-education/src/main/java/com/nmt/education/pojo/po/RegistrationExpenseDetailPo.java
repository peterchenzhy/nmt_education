package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value="com-nmt-education-pojo-po-RegistrationExpenseDetailPo")
@Getter
@Setter
@ToString
public class RegistrationExpenseDetailPo {
    /**
    * 主键id
    */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
    * 课程id
    */
    @ApiModelProperty(value="课程id")
    private Long registrationId;

    /**
    * 费用类型
    */
    @ApiModelProperty(value="费用类型")
    private Integer feeType;

    /**
    * 费用状态
    */
    @ApiModelProperty(value="费用状态")
    private Integer feeStatus;

    /**
    * 费用金额
    */
    @ApiModelProperty(value="费用金额")
    private String amount;

    /**
    * 单项金额
    */
    @ApiModelProperty(value="单项金额")
    private String perAmount;

    /**
    * 数量
    */
    @ApiModelProperty(value="数量")
    private Integer count;

    /**
    * 折扣
    */
    @ApiModelProperty(value="折扣")
    private String discount;

    /**
    * 缴费方式
    */
    @ApiModelProperty(value="缴费方式")
    private Integer payment;

    /**
    * 有效：1 无效：0
    */
    @ApiModelProperty(value="有效：1 无效：0")
    private Integer status;

    /**
    * 备注
    */
    @ApiModelProperty(value="备注")
    private String remark;

    /**
    * 创建人
    */
    @ApiModelProperty(value="创建人")
    private Integer creator;

    /**
    * 创建时间
    */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
    * 更改人
    */
    @ApiModelProperty(value="更改人")
    private Integer operator;

    /**
    * 更改时间
    */
    @ApiModelProperty(value="更改时间")
    private Date operateTime;
}