package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CourseSchedulePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CourseSignInVo {

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;

    /**
     * 课程名字
     */
    @ApiModelProperty(value = "课程名字")
    private String courseName;

    /**
     * 课程日程
     */
    @ApiModelProperty(value = "课程日程")
    private CourseSchedulePo courseSchedule;

    /**
     * 签到信息
     */
    @ApiModelProperty(value = "签到信息")
    private List<CourseSignInItem> signInVos;


}
