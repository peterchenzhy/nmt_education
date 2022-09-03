package com.nmt.education.service.export;

import com.alibaba.excel.converters.bigdecimal.BigDecimalStringConverter;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.NumberUtils;

import java.math.BigDecimal;

/**
 * 0.9899-->98.99%
 */
public class RatioStringConverter extends BigDecimalStringConverter {

    String SUFFIX = "%";
    BigDecimal bigDecimal_100 = new BigDecimal(100);


    @Override
    public CellData convertToExcelData(BigDecimal value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        BigDecimal multiply = value.multiply(bigDecimal_100);
        String str = multiply.setScale(2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        return new CellData(str+SUFFIX);
    }
}
