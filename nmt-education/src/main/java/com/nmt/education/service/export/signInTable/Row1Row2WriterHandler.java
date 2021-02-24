package com.nmt.education.service.export.signInTable;

import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Objects;

/**
 * 居中显示课程名称 ，单元格合并
 */
public class Row1Row2WriterHandler extends AbstractRowWriteHandler {

    private int maxCellSize;

    public Row1Row2WriterHandler(int maxCellSize) {
        this.maxCellSize = maxCellSize;
    }


    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (row.getRowNum() > 1) {
            return;
        }
        CellStyle cellStyle = StyleUtil.buildContentCellStyle(writeSheetHolder.getSheet().getWorkbook(), CellStyleUtil.headCellStyle());

        //补足单元格，不然合并的时候样式会有问题
        for (int i = 0; i < maxCellSize; i++) {
            Cell cell = row.getCell(i);
            if (Objects.isNull(cell)) {
                cell = row.createCell(i);
            }
            cell.setCellStyle(cellStyle);
        }


        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, maxCellSize - 1);
        writeSheetHolder.getSheet().addMergedRegion(region);

    }

}
