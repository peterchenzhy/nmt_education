package com.nmt.education.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.nmt.education.service.export.signInTable.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EasyExcelTest {
    public static void main(String[] args) {
        String fileStr = "C:\\Users\\PeterChen\\Desktop\\test_aa.xlsx";
        File file = getFile(fileStr);

        ExcelWriter excelWriter = EasyExcel.write(file).build();

//        HorizontalCellStyleStrategy styleStrategy =
//                new HorizontalCellStyleStrategy(tableHead(), tableHead());

        WriteSheet test_sheet1 = new ExcelWriterSheetBuilder(excelWriter)
                .sheetName("test_Sheet1").needHead(false)
                .registerWriteHandler(new Row1Row2WriterHandler(10))
                .registerWriteHandler(new Row3Row4WriterHandler(10
                        , Lists.newArrayList("1", "2"), Lists.newArrayList("20201211", "20201220")))
                .registerWriteHandler(new DataRowWriterHandler(10,Lists.newArrayList(new TableData("1", "peter"), new TableData("2", "son"))))
                .build();


        List<SignInTableExportService.TableHead1> head = new ArrayList<>();
        SignInTableExportService.TableHead1 h1 = new SignInTableExportService.TableHead1("");
        h1.setHead("语文课");
        head.add(h1);

        SignInTableExportService.TableHead1 h2 = new SignInTableExportService.TableHead1("");
        h2.setHead("上课时间");
        head.add(h2);
        excelWriter.write(Lists.newArrayList(head), test_sheet1);


//        excelWriter.write(Lists.newArrayList("head"),test_sheet1);
        excelWriter.write(Lists.newArrayList(head), test_sheet1);
        excelWriter.write(Lists.newArrayList(head), test_sheet1);


        excelWriter.finish();


    }


    private static File getFile(String fileStr) {
        File file = new File(fileStr);
        if (file.exists()) {
            file.delete();
            return new File(fileStr);
        }
        return file;
    }
}
