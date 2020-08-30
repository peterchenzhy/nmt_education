package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value="com-nmt-education-pojo-po-StudentAccountPo")
@Getter
@Setter
@ToString
public class StudentAccountPo {
    /**
    * 主键id
    */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
    * 员工id
    */
    @ApiModelProperty(value="员工id")
    private Long userId;

    /**
    * 工号
    */
    @ApiModelProperty(value="工号")
    private Integer userCode;

    /**
    * 金额
    */
    @ApiModelProperty(value="金额")
    private String amount;

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
    * 版本号
    */
    @ApiModelProperty(value="版本号")
    private Integer version;
}