package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value="com-nmt-education-pojo-po-StudentAccountFlowPo")
@Getter
@Setter
@ToString
public class StudentAccountFlowPo {
    /**
    * 主键id
    */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
    * 账户id
    */
    @ApiModelProperty(value="账户id")
    private Long studentAccountId;

    /**
    * 操作类型
    */
    @ApiModelProperty(value="操作类型")
    private Integer type;

    /**
    * 关联id
    */
    @ApiModelProperty(value="关联id")
    private Long refId;

    /**
    * 金额
    */
    @ApiModelProperty(value="金额")
    private String amount;

    /**
    * 修改前金额
    */
    @ApiModelProperty(value="修改前金额")
    private String beforeAmount;

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

    /**
     * @see com.nmt.education.commmons.AccountFlowSourceEnum
     * 数据来源；结转：0；退费：1；编辑：2
     * 消耗时的source为-1
     */
    private Integer source ;
}