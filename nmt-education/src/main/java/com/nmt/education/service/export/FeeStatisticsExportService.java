package com.nmt.education.service.export;

import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import com.nmt.education.service.statistics.FeeStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeStatisticsExportService extends AbstractExportService<FeeStatisticsReqDto, FeeStatisticsVo> {
    @Autowired
    private FeeStatisticsService feeStatisticsService;



    @Override
    public  List<FeeStatisticsVo> getDataList(FeeStatisticsReqDto dto, Integer logInUser) {
        List<FeeStatisticsVo> dataList = feeStatisticsService.exportList(dto, logInUser);
        return dataList;
    }

    @Override
    public String getFileName() {
        return "费用统计报表";
    }

    @Override
    public Class getExportClass() {
        return FeeStatisticsVo.class;
    }
}
