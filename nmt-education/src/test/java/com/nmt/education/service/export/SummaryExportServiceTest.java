package com.nmt.education.service.export;

import com.nmt.education.BaseTest;
import com.nmt.education.pojo.dto.req.SummaryExportReqDto;
import com.nmt.education.pojo.vo.SummaryExportDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryExportServiceTest extends BaseTest {

    @Autowired
    private SummaryExportService summaryExportService;

    @Test
    void getDataList1() {
        SummaryExportReqDto dto = new SummaryExportReqDto();
        dto.setCourseSubject(1);
//        dto.setCourseType(copy.getCourseType());
        dto.setSeason(3);
        dto.setYear(2020);
        dto.setCampus(1);

        Integer logInUser = 10000001 ;
        List<SummaryExportDto> dataList = summaryExportService.getDataList(dto, logInUser);
        System.out.println(dataList);
    }
}