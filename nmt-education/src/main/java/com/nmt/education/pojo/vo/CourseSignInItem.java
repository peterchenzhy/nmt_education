package com.nmt.education.pojo.vo;

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
    private Long studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 签到
     */
    private Integer signIn;

    /**
     * 报名信息id
     */
    private Long registerSummaryId;

    /**
     * 课程id
     */
    private Long courseId ;

    /**
     * 课程id
     */
    private Long courseScheduleId ;
}
