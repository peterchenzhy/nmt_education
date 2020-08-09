package com.nmt.education.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SignRecordVo {

    @ApiModelProperty(value = "学生id")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名")
    private String name;
    @ApiModelProperty(value = "学生code")
    private String code;

    @ApiModelProperty(value = "学校")
    private String school;
    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "联系人1电话,")
    private String phone;

    @ApiModelProperty(value = "年级 小学初中1-9 高中10-12 ")
    private Integer grade;

    @ApiModelProperty(value = "课程报名id")
    private Long courseRegisterId ;

    @ApiModelProperty(value = "签到数据,key:courseScheduleId ，value：签到对象")
    Map<Long,SignInfo> signInMap = new HashMap<>();

    @Setter
    @Getter
    @AllArgsConstructor
    public static class SignInfo{

        @ApiModelProperty(value = "签到")
        private Integer signIn ;
        @ApiModelProperty(value = "签到备注")
        private String signInRemark;
        @ApiModelProperty(value = "报名信息id")
        private Long registerSummaryId;
    }
}
