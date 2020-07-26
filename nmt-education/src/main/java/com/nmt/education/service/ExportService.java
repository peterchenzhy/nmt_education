package com.nmt.education.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import com.nmt.education.service.statistics.FeeStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {
    @Autowired
    private FeeStatisticsService feeStatisticsService;

    /**
     * 费用记录报表导出
     *
     * @param dto
     * @param logInUser
     * @param response
     */
    public void exportFeeStatistics(FeeStatisticsReqDto dto, Integer logInUser, HttpServletResponse response) throws IOException {
        String fileName = "费用记录报表";

        //设置response
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //设置header
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteFont.setBold(false);
        headWriteCellStyle.setWriteFont(headWriteFont);

        //设置内容格式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        // 设置handler
        HorizontalCellStyleStrategy styleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        //GetData
        List<FeeStatisticsVo> dataList = feeStatisticsService.exportList(dto, logInUser);

        //写excel
        EasyExcel.write(response.getOutputStream(), FeeStatisticsVo.class)
                .sheet("下载excel服务")
                .registerWriteHandler(styleStrategy)
                .doWrite(dataList);

    }
}
