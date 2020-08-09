package com.nmt.education.service.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public abstract class AbstractExportService<E, T> {

    /**
     * 费用记录报表导出
     *
     * @param dto
     * @param logInUser
     * @param response
     */
    public void doExport(E dto, Integer logInUser, HttpServletResponse response) throws IOException {

        //设置response
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + getFileName() + ".xlsx");
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
        List<T> dataList = getDataList(dto, logInUser);

        //写excel
        EasyExcel.write(response.getOutputStream(), getExportClass())
                .sheet("sheet1")
                .registerWriteHandler(styleStrategy)
                .doWrite(dataList);

    }

    public abstract List<T> getDataList(E dto, Integer logInUser);

    public abstract String getFileName();

    public abstract Class getExportClass();

}
