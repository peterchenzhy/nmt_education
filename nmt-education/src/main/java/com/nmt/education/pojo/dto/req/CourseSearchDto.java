package com.nmt.education.pojo.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class CourseSearchDto {

    /**
     * 课程科目
     */
    @ApiModelProperty(value = "课程科目")
    private Integer courseSubject;

    /**
     * 课程类型
     */
    @ApiModelProperty(value = "课程类型")
    private Integer courseType;
    /**
     * 年级
     */
    @ApiModelProperty(value = "年级")
    private Integer grade;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private Date startDate;

    /**
     * 教师id
     */
    @ApiModelProperty(value = "教师id")
    private Long teacherId;

    /**
     * 校区
     */
    @ApiModelProperty(value = "校区")
    private Integer campus;
    /**
     * 年度
     */
    @ApiModelProperty(value = "年度")
    private Integer year;
    /**
     * 季度
     */
    @ApiModelProperty(value = "季度")
    private Integer season;
    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    @ApiModelProperty(value = "分页 页码")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "分页 每页数量")
    private Integer pageSize = 10;


}