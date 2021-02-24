package com.nmt.education.service.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.write.style.ContentLoopMerge;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


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

        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), getExportClass())
                .registerWriteHandler(styleStrategy).build();

        //获取数据
        List<T> dataList = getDataList(dto, logInUser);
        //写excel
        if (isWriteSheetBySheet()) {
            //如果是一个一个sheet写，那么sheet按照排序字段，进行排序
            Field field = getField(getSheetSplitFieldName());
            Field sortField = getField(getSheetSortFieldName());
            TreeMap<Integer, Map<String, List<T>>> dataMap = dataList.stream().collect(
                    Collectors.groupingBy(k -> (Integer) ReflectionUtils.getField(sortField, k), TreeMap::new,
                            Collectors.groupingBy(k -> (String) ReflectionUtils.getField(field, k))));

            dataMap.forEach((sort, sortData) -> {
                sortData.forEach((sheetName, data) -> {
                    ExcelWriterSheetBuilder excelWriterSheetBuilder = new ExcelWriterSheetBuilder(excelWriter);
                    excelWriterSheetBuilder.sheetName(sheetName.replace("/", "_"));
                    excelWriter.write(data, excelWriterSheetBuilder.build());
                });
            });
        } else {
            excelWriter.write(dataList, EasyExcel.writerSheet("sheet1").build());
        }
        excelWriter.finish();

    }


    //获取数据
    public abstract List<T> getDataList(E dto, Integer logInUser);

    //获取文件名
    public abstract String getFileName();

    //获取拆分sheet字段名，即sheet名字段
    protected String getSheetSplitFieldName() {
        return null;
    }

    //获取sheet排序字段
    //字段类型必须是int型
    protected String getSheetSortFieldName() {
        return null;
    }

    //获取导出类
    protected Class getExportClass() {
        Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[1];
        return (Class<T>) actualTypeArgument;
    }


    //是否一个一个sheet写 默认为否
    protected boolean isWriteSheetBySheet() {
        return false;
    }


    //反射获取拆分字段
    private Field getField(String fieldName) {
        Assert.notNull(fieldName, "fieldName 字段不能为空");
        Field field = ReflectionUtils.findField(getExportClass(), fieldName);
        field.setAccessible(true);
        return field;
    }


}
