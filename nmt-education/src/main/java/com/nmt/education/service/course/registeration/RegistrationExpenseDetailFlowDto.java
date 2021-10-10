package com.nmt.education.service.course.registeration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistrationExpenseDetailFlowDto {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 报名id
     */
    private Long registrationId;

    /**
     * 缴费id
     */
    private Long registerExpenseDetailId;

    /**
     * 金额
     */
    private String amount;





}