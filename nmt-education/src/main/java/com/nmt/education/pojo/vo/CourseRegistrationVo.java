package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.CourseSchedulePo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
public class CourseRegistrationVo extends CourseRegistrationPo {


    @ApiModelProperty(value = "课程对象")
    private CoursePo course;

    @ApiModelProperty(value = "学生对象")
    private StudentVo student;

    @ApiModelProperty(value = "报名时间")
    private Date registerTime;
    @ApiModelProperty(value = "课时信息")
    private List<CourseSchedulePo> courseScheduleList = Collections.emptyList();

    @ApiModelProperty(value = "缴费信息")
    private List<RegistrationExpenseDetailPo> registerExpenseDetail =Collections.emptyList();
}
