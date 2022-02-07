package com.nmt.education.service.export.signInTable;

import com.alibaba.excel.util.StyleUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 居中显示课程名称 ，单元格合并
 */
public class DataRowWriterHandler extends AbstractRowWriteHandler {


    Map<String, TableData> dataMap;
    private int maxCellSize;

    public DataRowWriterHandler(int maxCellSize,List<TableData> dataList) {
        this.dataMap = dataList.stream().collect(Collectors.toMap(TableData::getNum, v -> v));
        this.maxCellSize =   maxCellSize;
    }


    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {

        if (row.getRowNum() < 4) {
            return;
        }
        CellStyle cellStyle = StyleUtil.buildContentCellStyle(writeSheetHolder.getSheet().getWorkbook(), CellStyleUtil.dataCellStyle());

        //补足单元格，不然合并的时候样式会有问题
        for (int i = 0; i < maxCellSize; i++) {
            Cell cell = row.getCell(i);
            if (Objects.isNull(cell)) {
                cell = row.createCell(i);
            }
            cell.setCellStyle(cellStyle);
        }
        TableData tableData = dataMap.get(String.valueOf(row.getRowNum()-3));

        if(tableData!=null) {
            row.getCell(0).setCellValue(tableData.getNum());
            row.getCell(1).setCellValue(tableData.getName());
        }else {
            row.getCell(0).setCellValue(row.getRowNum()-3);
        }


    }


}
