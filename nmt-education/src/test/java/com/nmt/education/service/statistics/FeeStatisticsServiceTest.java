package com.nmt.education.service.statistics;

import com.nmt.education.BaseTest;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class FeeStatisticsServiceTest extends BaseTest {

    @Autowired
    private FeeStatisticsService feeStatisticsService;
    @Test
    void summary1() {
        FeeStatisticsReqDto reqDto = new FeeStatisticsReqDto();
        reqDto.setStartDate(DateUtil.parseDate("2020-9-10"));
//        reqDto.setEndDate(copy.getEndDate());
//        reqDto.setCampus(copy.getCampus());
//        reqDto.setFeeFlowType(copy.getFeeFlowType());
//        reqDto.setPageNo(copy.getPageNo());
//        reqDto.setPageSize(copy.getPageSize());
//        System.out.println( feeStatisticsService.summary(reqDto,11000000));
    }
}