package com.nmt.education.compare;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ExcelTest
 *
 * @author PeterChen
 * @summary ExcelTest
 * @Copyright (c) 2022, PeterChen All Rights Reserved.
 * @Description ExcelTest
 * @since 2022-01-21 01:42
 */
public class ExcelTest {

   private static  String file = "C:\\Users\\PeterChen\\Desktop\\compare.xls";

    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
        //yan
        Sheet sheet0 = workbook.getSheetAt(0);
        List<Data> yanData = new ArrayList<>();
        for (int i = 1 ; i<=sheet0.getLastRowNum();i++){
            Row row = sheet0.getRow(i);
            Data data = new Data();
            data.setName(row.getCell(0).toString());
            data.setAmount( new BigDecimal(row.getCell(1).toString()));
            yanData.add(data);
        }
        Map<String, Data> yanMap = yanData.stream().collect(Collectors.toMap(k -> k.getName(), v -> v, (v1, v2) -> {
            v1.setAmount(v1.getAmount().add(v2.getAmount()));
            return v1;
        }));

        //---------------
        Sheet sheet1 = workbook.getSheetAt(1);
        List<Data> sysData = new ArrayList<>();
        for (int i = 1 ; i<=sheet1.getLastRowNum();i++){
            Row row = sheet1.getRow(i);
            Data data = new Data();
            data.setName(row.getCell(0).toString());
            data.setAmount( new BigDecimal(row.getCell(1).toString()));
            sysData.add(data);
        }
        Map<String, Data> sysMap = sysData.stream().collect(Collectors.toMap(k -> k.getName(), v -> v, (v1, v2) -> {
                v1.setAmount(v1.getAmount().add(v2.getAmount()));
            return v1;
        }));

        BigDecimal sysTotal = sysMap.values().stream().map(Data::getAmount).reduce(BigDecimal.ZERO ,BigDecimal::add);
        BigDecimal excelTotal=yanMap.values().stream().map(Data::getAmount).reduce(BigDecimal.ZERO ,BigDecimal::add);

        System.out.println("systotal:"+sysTotal.stripTrailingZeros().toPlainString());
        System.out.println("excelTotal:"+excelTotal.stripTrailingZeros().toPlainString());
        System.out.println("==============================");

        sysMap.forEach((u,v)->{
            Data data = yanMap.get(u);
            if(Objects.isNull(data)){
                System.out.println("sys:" + v + "存在");
            }else {
                if (v.getAmount().compareTo(data.getAmount()) != 0) {
                    System.out.println("sys:" + v + "不匹配" + data);
                }
            }
        });

        System.out.println("==============================");

        yanMap.forEach((u,v)->{
            Data data = sysMap.get(u);
            if(Objects.isNull(data)){
                System.out.println("yan:" + v + "存在");
            }else {
                if (v.getAmount().compareTo(data.getAmount()) != 0) {
                    System.out.println("yan:" + v + "不匹配" + data);
                }
            }
        });

    }

    @lombok.Data
    static class Data{
        private String name ;

        private BigDecimal amount;
    }
}
