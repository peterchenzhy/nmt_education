package com.nmt.education.service.export.signInTable;

import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.Objects;

/**
 * 居中显示课程名称 ，单元格合并
 */
public class Row3Row4WriterHandler extends AbstractRowWriteHandler {

    private int maxCellSize;
    private List<String> times = Lists.newArrayList("课时","");
    private List<String> dateList  = Lists.newArrayList("上课日期","");

    public Row3Row4WriterHandler(int maxCellSize, List<String> times, List<String> dateList) {
        this.maxCellSize = maxCellSize;
        this.times .addAll( times);
        this.dateList.addAll(dateList)  ;
    }


    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {

        if (row.getRowNum() > 3 || row.getRowNum() < 2) {
            return;
        }
        CellStyle cellStyle = StyleUtil.buildContentCellStyle(writeSheetHolder.getSheet().getWorkbook(), CellStyleUtil.defaultCellStyle());

        //补足单元格，不然合并的时候样式会有问题
        for (int i = 0; i < maxCellSize; i++) {
            Cell cell = row.getCell(i);
            if (Objects.isNull(cell)) {
                cell = row.createCell(i);
            }
            cell.setCellStyle(cellStyle);
            if(times.size()>i){
                if (row.getRowNum() == 2) {
                    cell.setCellValue(times.get(i));
                } else {
                    cell.setCellValue(dateList.get(i));
                }
            }

        }


        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1);
        writeSheetHolder.getSheet().addMergedRegion(region);

    }

}
