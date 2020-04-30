package com.nmt.education.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value="com-nmt-education-pojo-po-RegisterationSummaryPo")
@Getter
@Setter
@ToString
public class RegisterationSummaryPo {
    /**
    * 主键id
    */
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
    * 学生id
    */
    @ApiModelProperty(value="学生id")
    private Long studentId;

    /**
    * 课程id
    */
    @ApiModelProperty(value="课程id")
    private Long courseId;

    /**
    * 课程时间id
    */
    @ApiModelProperty(value="课程时间id")
    private Long courseScheduleId;

    /**
    * 报名记录id
    */
    @ApiModelProperty(value="报名记录id")
    private Long courseRegistrationId;

    /**
    * 签到状态
    */
    @ApiModelProperty(value="签到状态")
    private Integer signIn;

    /**
    * 有效：1 无效：0
    */
    @ApiModelProperty(value="有效：1 无效：0")
    private Integer status;

    /**
    * 创建人
    */
    @ApiModelProperty(value="创建人")
    private Integer creator;

    /**
    * 创建时间
    */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
    * 更改人
    */
    @ApiModelProperty(value="更改人")
    private Integer operator;

    /**
    * 更改时间
    */
    @ApiModelProperty(value="更改时间")
    private Date operateTime;
}