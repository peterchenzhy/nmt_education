package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringNumberConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ScheduleSignInSummaryDto {

    @ExcelProperty(value = "所在校区", index = 0)
    private String campusStr;

    @ExcelProperty(value = "课程名字", index = 1)
    private String courseName;

    @ExcelProperty(value = "科目", index = 2)
    private String subject;

    @ExcelProperty(value = "季节", index = 3)
    private String seasonStr;

    @ExcelProperty(value = "学生姓名", index = 4)
    private String studentName;

    @ExcelProperty(value = "年级", index = 5)
    private String gradeStr;

    @ExcelProperty(value = "签到状态", index = 6)
    private String signInStr;

    @ExcelProperty(value = "上课时间", index = 7)
    private Date courseDatetime;

    @ExcelProperty(value = "费用", index = 8, converter = StringNumberConverter.class)
    private String price;


    @ExcelProperty(value = "统计开始日期", index = 9)
    private Date startDate;

    @ExcelProperty(value = "统计结束日期", index = 10)
    private Date endDate;

    @ExcelIgnore
    private Long courseId;

    @ExcelIgnore
    private Integer campus;

    @ExcelIgnore
    private Integer courseSubject;
    @ExcelIgnore
    private Integer signIn;
    @ExcelIgnore
    private Integer grade;
    @ExcelIgnore
    private Integer season;

    @ExcelIgnore
    private Long courseRegistrationId ;




}
