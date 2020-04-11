package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumVo {

    /**
     * 显示值
     */
    @ApiModelProperty(value = "显示值")
    private String label;
    /**
     * 实际值
     */
    @ApiModelProperty(value = "实际值")
    private Integer value;

    /**
     * 是否是dbConfig
     */
    @ApiModelProperty(value = "是否时存储在db的枚举")
    private boolean dbConfig;

    /**
     * 类型
     */
    @ApiModelProperty(value = "枚举类型")
    private Integer type;

    /**
     * 类型code
     */
    @ApiModelProperty(value = "枚举类型code")
    private String typeCode;

    /**
     * 类型描述
     */
    @ApiModelProperty(value = "枚举类型描述")
    private String typeDesc;

    /**
     * icon
     */
    @ApiModelProperty(value = "icon前端用")
    private String icon;

}
