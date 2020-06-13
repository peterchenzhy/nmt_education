package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CourseSignInItem {

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id")
    private Long studentId;
    /**
     * 学生姓名
     */
    @ApiModelProperty(value = "学生姓名")
    private String studentName;
    /**
     * 签到
     */
    @ApiModelProperty(value = "签到")
    private Integer signIn;

    /**
     * 报名信息id
     */
    @ApiModelProperty(value = "报名信息id")
    private Long registerSummaryId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId ;

    /**
     * 课程日程id
     */
    @ApiModelProperty(value = "课程日程id")
    private Long courseScheduleId ;

    /**
     *签到备注
     */
    @ApiModelProperty(value = "签到备注")
    private String signInRemark="" ;
}
