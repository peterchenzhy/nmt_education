package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value="com-nmt-education-pojo-po-TeacherSalaryConfigPo")
@Getter
@Setter
@ToString
public class TeacherSalaryConfigPo {
    /**
    * 主键id
    */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
    * 教师id
    */
    @ApiModelProperty(value="教师id")
    private Long teacherId;

    /**
    * 年级
    */
    @ApiModelProperty(value="年级")
    private Integer grade;

    /**
    * 课程科目
    */
    @ApiModelProperty(value="课程科目")
    private Integer courseSubject;

    /**
    * 课程类型
    */
    @ApiModelProperty(value="课程类型")
    private Integer courseType;

    /**
    * 课程单价
    */
    @ApiModelProperty(value="课程单价")
    private String unitPrice;

    /**
    * 备注
    */
    @ApiModelProperty(value="备注")
    private String remark;

    /**
    * 有效：1 无效：0
    */
    @ApiModelProperty(value="有效：1 无效：0")
    private Integer status;

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