package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CourseSchedulePo {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "课程日程主键id")
    private Long id;

    /**
     * 教师id
     */
    @ApiModelProperty(value = "教师id")
    private Long teacherId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;

    /**
     * 课时次数
     */
    @ApiModelProperty(value = "课时次数")
    private Integer courseTimes;

    /**
     * 上课时间
     */
    @ApiModelProperty(value = "上课时间")
    private Date courseDatetime;

    /**
     * 教师价格
     */
    @ApiModelProperty(value = "教师价格")
    private String teacherPrice;

    /**
     * 时长(分钟)
     */
    @ApiModelProperty(value = "时长(分钟)")
    private Integer perTime;

    /**
     * 签到状态
     */
    @ApiModelProperty(value = "课程签到状态")
    private Integer signIn;

    /**
     * 有效：1 无效：0
     */
    private Integer status;

    /**
     * 创建人
     */
    private Integer creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更改人
     */
    private Integer operator;

    /**
     * 更改时间
     */
    private Date operateTime;
}