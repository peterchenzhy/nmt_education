package com.nmt.education.service.export.signInTable;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.HashMap;
import java.util.Map;

public class CellStyleUtil {

    private static Map<String ,WriteCellStyle> writeCellStyleMap = new HashMap<>();

    private static String DEFAULT_CELL_STYLE = "default_cell_style";
    private static String HEAD_CELL_STYLE = "head_cell_style";
    /**
     * 表头 style
     *
     * @return
     */
    public static WriteCellStyle headCellStyle() {

        WriteCellStyle writeCellStyle = writeCellStyleMap.get(HEAD_CELL_STYLE);
        if(writeCellStyle!=null){
            return writeCellStyle;
        }

        //设置header
        WriteCellStyle defaultWriteCell = new WriteCellStyle();
        defaultWriteCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 15);
        headWriteFont.setBold(true);
        defaultWriteCell.setWriteFont(headWriteFont);

        setBorderStyle(defaultWriteCell);

        writeCellStyleMap.put(HEAD_CELL_STYLE,defaultWriteCell);
        return defaultWriteCell;
    }

    /**
     * 默认 style
     * @return
     */
    public static WriteCellStyle defaultCellStyle() {

        WriteCellStyle writeCellStyle = writeCellStyleMap.get(DEFAULT_CELL_STYLE);
        if(writeCellStyle!=null){
            return writeCellStyle;
        }

        //设置header
        WriteCellStyle defaultWriteCell = new WriteCellStyle();
        defaultWriteCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteFont.setBold(false);
        defaultWriteCell.setWriteFont(headWriteFont);

        setBorderStyle(defaultWriteCell);

        writeCellStyleMap.put(DEFAULT_CELL_STYLE,defaultWriteCell);
        return defaultWriteCell;
    }


    /**
     * 默认边框样式
     *
     * @param writeCellStyle
     */
    private static void setBorderStyle(WriteCellStyle writeCellStyle) {
        //设置边框样式
        writeCellStyle.setBorderLeft(BorderStyle.THIN);
        // writeCellStyle.setBottomBorderColor(IndexedColors.BLUE.getIndex()); //颜色
        writeCellStyle.setBorderTop(BorderStyle.THIN);
        writeCellStyle.setBorderRight(BorderStyle.THIN);
        writeCellStyle.setBorderBottom(BorderStyle.THIN);

    }
}
