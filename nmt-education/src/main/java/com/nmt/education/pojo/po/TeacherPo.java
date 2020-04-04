package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "com-nmt-education-pojo-po-TeacherPo")
@Getter
@Setter
@ToString
public class TeacherPo {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 老师姓名
     */
    @ApiModelProperty(value = "老师姓名")
    private String name;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日")
    private Date birthday;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Boolean sex;

    /**
     * 任职学校
     */
    @ApiModelProperty(value = "任职学校")
    private String school;

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