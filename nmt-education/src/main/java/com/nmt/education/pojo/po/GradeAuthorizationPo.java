package com.nmt.education.pojo.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 校区授权
 * @TableName grade_authorization
 */
@Data
public class GradeAuthorizationPo implements Serializable {
    /**
     * 主键id
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Long id;

    /**
     * 名称
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private String name;

    /**
     * code
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Integer code;

    /**
     * 工号
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Integer userId;

    /**
     * 备注
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private String remark;

    /**
     * 有效：1 无效：0
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Boolean status;

    /**
     * 创建人
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Integer creator;

    /**
     * 创建时间
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Date createTime;

    /**
     * 更改人
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Integer operator;

    /**
     * 更改时间
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private Date operateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table grade_authorization
     *
     * @mbg.generated 2021-06-11 16:04:11
     */
    private static final long serialVersionUID = 1L;
}