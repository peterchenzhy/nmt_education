package com.nmt.education.pojo.po;

import java.util.Date;
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
    private Long id;

    /**
    * 教师id
    */
    private Long teacherId;

    /**
    * 课程编码 
    */
    private String code;

    /**
    * 年度
    */
    private Integer year;

    /**
    * 班次 春季班秋季班
    */
    private Byte season;

    /**
    * 校区
    */
    private Byte campus;

    /**
    * 课程类型
    */
    private Byte courseType;

    /**
    * 课程科目
    */
    private Byte courseSubject;

    /**
    * 课程归类
    */
    private Byte courseClassification;

    /**
    * 课程名称
    */
    private String name;

    /**
    * 年级
    */
    private Byte grade;

    /**
    * 课程状态
    */
    private Byte courseStatus;

    /**
    * 教室id
    */
    private Long classroom;

    /**
    * 满员人数
    */
    private Integer totalStudent;

    /**
    * 每节课时间 (单位分钟)
    */
    private Integer perTime;

    /**
    * 设计课次
    */
    private Byte times;

    /**
    * 开始日期
    */
    private Date startDate;

    /**
    * 结束日期
    */
    private Date endDate;

    /**
    * 课程描述
    */
    private String description;

    /**
    * 备注
    */
    private String remark;

    /**
    * 有效：1 无效：0
    */
    private Boolean status;

    /**
    * 创建人
    */
    private Integer creator;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 更改人
    */
    private Integer operator;

    /**
    * 更改时间
    */
    private Date operateTime;
}