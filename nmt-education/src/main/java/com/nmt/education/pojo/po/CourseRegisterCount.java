package com.nmt.education.pojo.po;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseRegisterCount {
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 报名人数
     */
    private int count ;
}
