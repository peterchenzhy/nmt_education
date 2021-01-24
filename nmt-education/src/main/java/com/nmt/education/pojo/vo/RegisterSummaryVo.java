package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CourseRegistrationPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Getter
@Setter
@ToString
public class RegisterSummaryVo extends CourseRegistrationPo {


    @ApiModelProperty(value = "课程名字")
    private String courseName ;

    @ApiModelProperty(value = "学生姓名")
    private String studentName ;

    @ApiModelProperty(value = "报名时间")
    private Date registerTime ;

    @ApiModelProperty(value = "签到状态")
    private Integer signIn ;

    @ApiModelProperty(value = "校区")
    private Integer campus;
    @ApiModelProperty(value = "科目")
    private Integer courseSubject ;
    @ApiModelProperty(value = "年级")
    private Integer grade;
    @ApiModelProperty(value = "上课时间")
    private Date courseDatetime;
    @ApiModelProperty(value = "季节")
    private Integer season;


}
