package com.nmt.education.pojo.po;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CoursePo {
    /**
    * 主键id
    */
    @ApiModelProperty(value = "数据id")
    private Long id;

    /**
    * 教师id
    */
    @ApiModelProperty(value = "老师id")
    private Long teacherId;

    /**
    * 课程编码 
    */
    @ApiModelProperty(value = "课程编码")
    private String code;

    /**
    * 年度
    */
    @ApiModelProperty(value = "年度")
    private Integer year;

    /**
    * 班次 春季班秋季班
    */
    @ApiModelProperty(value = " 班次 春季班秋季班")
    private Integer season;

    /**
    * 校区
    */
    @ApiModelProperty(value = "校区")
    private Integer campus;

    /**
    * 课程类型
    */
    @ApiModelProperty(value = "课程类型")
    private Integer courseType;

    /**
    * 课程科目
    */
    @ApiModelProperty(value = "课程科目")
    private Integer courseSubject;

    /**
    * 课程归类
    */
    @ApiModelProperty(value = "课程归类")
    private Integer courseClassification;

    /**
    * 课程名称
    */
    @ApiModelProperty(value = "课程名称")
    private String name;

    /**
    * 年级
    */
    @ApiModelProperty(value = "年级")
    private Integer grade;

    /**
    * 课程状态
    */
    @ApiModelProperty(value = "课程状态")
    private Integer courseStatus;

    /**
    * 教室id
    */
    @ApiModelProperty(value = "教室id")
    private Long classroom;

    /**
    * 满员人数
    */
    @ApiModelProperty(value = "满员人数")
    private Integer totalStudent;

    /**
    * 每节课时间 (单位分钟)
    */
    @ApiModelProperty(value = "每节课时间")
    private Integer perTime;

    /**
    * 设计课次
    */
    @ApiModelProperty(value = "设计课次")
    private Integer times;

    /**
    * 开始日期
    */
    @ApiModelProperty(value = "开始日期")
    private Date startDate;

    /**
    * 结束日期
    */
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    /**
    * 课程描述
    */
    @ApiModelProperty(value = "课程描述")
    private String description;

    /**
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
    * 有效：1 无效：0
    */
    @ApiModelProperty(value = "有效：1 无效：0")
    private Boolean status;

    /**
    * 创建人
    */
    @ApiModelProperty(value = "创建人")
    private Integer creator;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
    * 更改人
    */
    @ApiModelProperty(value = "更改人")
    private Integer operator;

    /**
    * 更改时间
    */
    @ApiModelProperty(value = "更改时间")
    private Date operateTime;
}