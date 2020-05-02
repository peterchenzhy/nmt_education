package com.nmt.education.pojo.po;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CourseExpensePo {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 属性  todo暂不启用 字段预留
     */
    @Deprecated
    private Integer prop=0;

    /**
     * 值
     */
    private String value;

    /**
     * 备注
     */
    private String remark="";

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