package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CourseSchedulePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class StudentCourseScheduleSummaryVo extends CourseSchedulePo {

    @ApiModelProperty(value = "学生签到状态")
    private Integer studentSignIn;

    @ApiModelProperty(value = "课程信息汇总表id")
    private Long registerSummaryId;

}
