package com.nmt.education.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FeeSummaryVo {
    //缴费
    private String pay;
    //退费
    private String refund;
    //课时费
    private String teacherPay;

    private String startDate ;
    private String endData ;
}
