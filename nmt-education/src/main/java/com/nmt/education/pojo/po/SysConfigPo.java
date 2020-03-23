package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "com-nmt-education-pojo-po-SysConfigPo")
@Getter
@Setter
@ToString
public class SysConfigPo implements Serializable {
    private static final long serialVersionUID = 5227261756210051958L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    /**
     * 类型code
     */
    @ApiModelProperty(value = "类型code")
    private String typeCode;

    /**
     * 类型描述
     */
    @ApiModelProperty(value = "类型描述")
    private String typeDesc;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    private Integer value;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

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