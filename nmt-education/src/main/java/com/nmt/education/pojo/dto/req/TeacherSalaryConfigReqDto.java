package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class TeacherSalaryConfigReqDto {


    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;
    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;
    /**
     * 教师id
     */
    @ApiModelProperty(value = "教师id",required = true)
    private Long teacherId;

    /**
     * 年级
     */
    @ApiModelProperty(value = "年级",required = true)
    private Integer grade;

    /**
     * 课程科目
     */
    @ApiModelProperty(value = "课程科目",required = true)
    private Integer courseSubject;

    /**
     * 课程类型
     */
    @ApiModelProperty(value = "课程类型",required = true )
    private Integer courseType;

    /**
     * 课程单价
     */
    @ApiModelProperty(value = "课程单价",required = true)
    private String unitPrice;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}