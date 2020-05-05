package com.nmt.education.pojo.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CourseScheduleReqDto {

    @ApiModelProperty(value = "数据id,仅编辑使用")
    private Long id;
    @ApiModelProperty(value = "编辑标志；0：无变化，1：新增，2：编辑，3：需要删除")
    private Integer editFlag = 0;

    /**
    * 教师id
    */
    @ApiModelProperty(value = "授课教师id")
    private Long teacherId;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Long courseId;

    /**
    * 课时次数
    */
    @ApiModelProperty(value = "课时次数")
    private Integer courseTimes;

    /**
    * 上课时间
    */
    @ApiModelProperty(value = "上课时间")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date courseDatetime;

    /**
    * 教师价格
    */
    @ApiModelProperty(value = "教师价格")
    private String teacherPrice;

    /**
    * 时长(分钟)
    */
    @ApiModelProperty(value = "时长(分钟)")
    private Integer perTime;

    /**
    * 签到状态
    */
    @ApiModelProperty(value = "签到状态0：未签到 1：已签到 ")
    private Integer signIn;
}