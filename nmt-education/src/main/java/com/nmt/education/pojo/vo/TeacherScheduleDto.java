package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringNumberConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeacherScheduleDto {

    @ExcelProperty(value = "课程名字", index = 0)
    private String courseName;

    @ExcelProperty(value = "科目", index = 1)
    private String subject;

    @ExcelProperty(value = "老师姓名", index = 2)
    private String teacherName;

    @ExcelProperty(value = "年级", index = 3)
    private String gradeStr;

    @ExcelProperty(value = "课时费", index = 4, converter = StringNumberConverter.class)
    private String teacherPrice;

    @ExcelProperty(value = "签到状态", index = 5)
    private String signIn;

    @ExcelProperty(value = "所在校区", index = 6)
    private String campusStr;

    @ExcelProperty(value = "上课时间", index = 7)
    private Date courseDate;

    @ExcelProperty(value = "统计开始日期", index = 8)
    private Date startDate;

    @ExcelProperty(value = "统计结束日期", index = 9)
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
    private int signInStatus;
    @ExcelIgnore
    private int grade;
}
