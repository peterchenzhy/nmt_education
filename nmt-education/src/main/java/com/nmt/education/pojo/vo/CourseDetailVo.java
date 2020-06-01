package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CourseExpensePo;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.CourseSchedulePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CourseDetailVo extends CoursePo {

    @ApiModelProperty(value = "课程费用配置")
    private List<CourseExpensePo> courseExpenseList = new ArrayList<>();
    @ApiModelProperty(value = "课程时间")
    private List<CourseSchedulePo> courseScheduleList = new ArrayList<>();
    @ApiModelProperty(value = "教师对象")
    private TeacherVo teacher;

    @ApiModelProperty(value = "最近一次上课日程")
    private CourseSchedulePo courseSchedule;
}
