package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringNumberConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeacherSalarySummaryDto {

    @ExcelProperty(value = "课程名字", index = 0)
    private String courseName;

    @ExcelProperty(value = "科目", index = 1)
    private String subject;

    @ExcelProperty(value = "老师姓名", index = 2)
    private String teacherName;

    @ExcelProperty(value = "年级", index = 3)
    private String gradeStr;

    @ExcelProperty(value = "每节课时间--分钟", index = 4)
    private Integer perTime;

    @ExcelProperty(value = "课次", index = 5)
    private Integer times = 1 ;

    @ExcelProperty(value = "单次课时费", index = 6, converter = StringNumberConverter.class)
    private String teacherPerPrice;

    @ExcelProperty(value = "总课时费", index = 7, converter = StringNumberConverter.class)
    private String teacherPrice;

    @ExcelProperty(value = "所在校区", index = 8)
    private String campusStr;

    @ExcelProperty(value = "统计开始日期", index = 9)
    private Date startDate;

    @ExcelProperty(value = "统计结束日期", index = 10)
    private Date endDate;

    @ExcelIgnore
    private Long courseId;

    @ExcelIgnore
    private Long teacherId;

    @ExcelIgnore
    private int campus;

    @ExcelIgnore
    private int courseSubject;

    @ExcelIgnore
    private int grade;
}
