package com.nmt.education.pojo.vo;

import com.nmt.education.pojo.po.CoursePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseVo  extends CoursePo {
    @ApiModelProperty(value = "报名人数")
    private int registerNum;
    @ApiModelProperty(value = "课程进度")
    private String progress ="-";

}
