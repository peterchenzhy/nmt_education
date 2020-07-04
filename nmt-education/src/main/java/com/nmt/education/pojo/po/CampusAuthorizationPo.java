package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value="com-nmt-education-pojo-po-CampusAuthorizationPo")
@Getter
@Setter
@ToString
public class CampusAuthorizationPo {
    /**
    * 主键id
    */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
    * 校区名称
    */
    @ApiModelProperty(value="校区名称")
    private String name;

    /**
    * code
    */
    @ApiModelProperty(value="code")
    private Integer code;

    /**
    * 工号
    */
    @ApiModelProperty(value="工号")
    private Integer userId;

    /**
    * 备注
    */
    @ApiModelProperty(value="备注")
    private String remark;

    /**
    * 有效：1 无效：0
    */
    @ApiModelProperty(value="有效：1 无效：0")
    private Boolean status;

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