package com.nmt.education.pojo.vo;

import com.nmt.education.commmons.NumberUtil;
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

    //报名人数
    private long registerStudentCount;

    //实际上交 = 总收入 - 总抵扣- 总退费
    private String actuallyHandIn;

    //总结余抵扣
    private String amountSummary;

    private String startDate ;
    private String endDate ;

    public void setActuallyHandIn(){
        this.actuallyHandIn = NumberUtil.String2Dec(this.pay)
                .subtract(NumberUtil.String2Dec(this.refund))
                .subtract(NumberUtil.String2Dec(this.amountSummary))
                .toPlainString();
    }
}
