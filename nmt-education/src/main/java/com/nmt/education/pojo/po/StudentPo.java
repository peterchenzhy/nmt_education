package com.nmt.education.pojo.po;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class StudentPo {
    /**
    * 主键id
    */
    private Long id;

    /**
    * 学生编码
    */
    private String studentCode;

    /**
    * 学生姓名
    */
    private String name;

    /**
    * 生日
    */
    private Integer birthday;

    /**
    * 学校
    */
    private String school;

    /**
    * 年级 小学初中1-9 高中10-12 
    */
    private Integer grade;

    /**
    * 联系人1
    */
    private String contact1;

    /**
    * 联系人2
    */
    private String contact2;

    /**
    * 联系人1电话

    */
    private String phone1;

    /**
    * 联系人2电话
    */
    private String phone2;

    /**
    * 备注
    */
    private String remark;

    /**
    * 有效：1 无效：0
    */
    private Integer status;

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