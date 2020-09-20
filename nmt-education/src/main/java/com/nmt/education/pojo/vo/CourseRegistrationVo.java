package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.*;
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

    @ApiModelProperty(value = "报名课时信息")
    private List<StudentCourseScheduleSummaryVo> courseScheduleList = Collections.emptyList();

    @ApiModelProperty(value = "缴费信息")
    private List<RegistrationExpenseDetailPo> registerExpenseDetail =Collections.emptyList();


    @ApiModelProperty(value = "支付流水")
    private List<ExpenseDetailFlowVo>  expenseDetailFlowVoList =Collections.emptyList();

    @ApiModelProperty(value = "结余消耗流水")
    private List<StudentAccountFlowPo> studentAccountFlowPoList = Collections.emptyList();

}
