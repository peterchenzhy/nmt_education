package com.nmt.education.pojo.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Collections;
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
public class CourseReqDto {

    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;
    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;

    @ApiModelProperty(value = "执教老师数据id")
    private Long teacherId;
    /**
     * 姓名
     */
    @NotNull(message = "年度不能为空")
    @ApiModelProperty(value = "年度", required = true)
    private Integer year;

    /**
     * 季节班次
     */
    @NotNull(message = "季节班次不能为空")
    @ApiModelProperty(value = "季节班次", required = true)
    private Integer season;
    /**
     * 校区
     */
    @NotNull(message = "校区不能为空")
    @ApiModelProperty(value = "校区", required = true)
    private Integer campus;

    /**
     * 课程类型
     */
    @NotNull(message = "课程类型不能为空")
    @ApiModelProperty(value = "课程类型", required = true)
    private Integer courseType;
    /**
     * 课程科目
     */
    @NotNull(message = "课程科目不能为空")
    @ApiModelProperty(value = "课程科目", required = true)
    private Integer courseSubject;

    /**
     * 课程归类
     */
    @NotNull(message = "课程归类不能为空")
    @ApiModelProperty(value = "课程归类", required = true)
    private Integer courseClassification;

    /**
     * 课程名称
     */
    @NotNull(message = "课程名称不能为空")
    @ApiModelProperty(value = "课程名称", required = true)
    private String name;

    /**
     * 年级
     */
    @NotNull(message = "年级名称不能为空")
    @ApiModelProperty(value = "年级", required = true)
    private Integer grade;

    /**
     * 教室  数据id
     */
    @ApiModelProperty(value = "教室")
    private Long classroom;

    /**
     * 满员人数
     */
    @ApiModelProperty(value = "满员人数")
    private Integer totalStudent;

    /**
     * 每节课时间 单位：分钟
     */
    @NotNull(message = "每节课时间不能为空")
    @ApiModelProperty(value = "每节课时间 单位：分钟", required = true)
    @Range(min = 1,message = "每节课时间必须设置")
    private Integer perTime;

    /**
     * 设计课次
     */
    @NotNull(message = "设计课次不能为空")
    @ApiModelProperty(value = "设计课次", required = true)
    @Range(min = 1,message = "设计课次必须大于0")
    private Integer times;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 课程描述
     */
    @ApiModelProperty(value = "课程描述")
    private String description;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "上课规律")
    private String courseRegular ;

    @ApiModelProperty(value = "课时信息",required = true)
    @NotNull(message = "课时信息不能为空")
    private List<CourseScheduleReqDto> courseScheduleList = Collections.emptyList();

    @ApiModelProperty(value = "费用列表",required = true)
    @NotNull(message = "费用列表不能为空")
    private List<CourseExpenseReqDto> courseExpenseList=Collections.emptyList();
}