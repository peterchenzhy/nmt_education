package com.nmt.education.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.nmt.education.pojo.vo.SummaryExportDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EasyExcelTest {
    public static void main(String[] args) {
        String fileStr = "C:\\Users\\PeterChen\\Desktop\\aa.xls";
        File file = new File(fileStr);
        ExcelWriter excelWriter = EasyExcel.write(file).build();

        for (int i= 1;i<=3;i++){
            List<SummaryExportDto>  list = new ArrayList<>();
            SummaryExportDto summaryExportDto = new SummaryExportDto();
            summaryExportDto.setCourseName(i+"--A");
            summaryExportDto.setCourseStatusStr(i+"--B");
            list.add(summaryExportDto);
            SummaryExportDto summaryExportDto1 = new SummaryExportDto();
            summaryExportDto1.setCourseName(i+"--C");
            summaryExportDto1.setCourseStatusStr(i+"--D");
            list.add(summaryExportDto1);

            ExcelWriterSheetBuilder excelWriterSheetBuilder = new ExcelWriterSheetBuilder(excelWriter);
            excelWriterSheetBuilder.sheetName(i+"--sheet");
            excelWriterSheetBuilder.head(SummaryExportDto.class);
            excelWriter.write(list,excelWriterSheetBuilder.build());

        }
        excelWriter.finish();


    }
}
