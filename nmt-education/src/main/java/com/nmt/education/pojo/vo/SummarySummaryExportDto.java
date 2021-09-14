package com.nmt.education.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringNumberConverter;
import lombok.Getter;
import lombok.Setter;

//消耗汇总表的 汇总表
@Getter
@Setter
public class SummarySummaryExportDto {

    //基本信息
    @ExcelProperty(value = "课程名字", index = 0)
    private String courseName;

    @ExcelProperty(value = "总消耗", index = 1, converter = StringNumberConverter.class)
    private String expired;

    @ExcelProperty(value = "未消耗", index = 2, converter = StringNumberConverter.class)
    private String unexpired;


}
