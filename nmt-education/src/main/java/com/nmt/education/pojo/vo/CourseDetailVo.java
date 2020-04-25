package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CourseExpensePo;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.CourseSchedulePo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CourseDetailVo extends CoursePo {

    private List<CourseExpensePo> courseExpenseList = new ArrayList<>();
    private List<CourseSchedulePo> courseScheduleList = new ArrayList<>();
}
