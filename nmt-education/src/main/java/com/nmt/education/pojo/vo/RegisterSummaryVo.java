package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CourseRegistrationPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

}
