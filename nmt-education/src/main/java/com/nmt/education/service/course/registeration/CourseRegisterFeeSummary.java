package com.nmt.education.service.course.registeration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class CourseRegisterFeeSummary {


    /**
     * 报名id
     */
    private Long courseId;

    /**
     * 金额
     */
    private BigDecimal totalAmount;

}
