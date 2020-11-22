package com.nmt.education.commmons;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shitiecheng
 * @version v1
 * @summary
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @since 2018/8/6
 */
public class NumberUtil {

    private final static Pattern PATTERN = Pattern.compile("-?[0-9]+.?[0-9]+");
    private final static Pattern PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$");

    /**
     * 判断是否数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static BigDecimal mutify(String... strs){
        if(Objects.isNull(strs)||strs.length==0){
            return BigDecimal.ZERO;
        }
        BigDecimal result = BigDecimal.ONE;
        for (String s : strs) {
            result = String2Dec(s).multiply(result);
        }
        return result;
    }




    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Matcher isNum = PATTERN.matcher(str);
        return isNum.matches();
    }

    public static void main(String[] args) {
        System.out.println(mutify("-2","5","0"));
    }
    /**
     * 超过11位，截取前11位
     * 不足11位前面补0
     *
     * @param str
     * @author PeterChen
     * @version v1
     * @since 2019/7/14 14:36
     */
    public static String length11(String str) {
        if (str.length() > 11) {
            return str.substring(0, 11);
        }
        String temp = "000000000000";
        String newStr = temp + str;
        return newStr.substring(newStr.length() - 11);
    }

    /**
     * 密码校验
     * 包含数字和大小写
     *
     * @param str 密码
     * @author PeterChen
     * @version v1
     * @since 2019/7/23 11:30
     */
    public static boolean passwordCheck(String str) {
        Matcher isNum = PASSWORD_PATTERN.matcher(str);
        return isNum.matches();
    }

    /**
     * 若传入dec为null 则返回0
     *
     * @param dec
     * @return
     */
    public static BigDecimal dec(BigDecimal dec) {
        if (Objects.isNull(dec)) {
            return BigDecimal.ZERO;
        } else {
            return dec;
        }
    }


    /**
     * String 转 dec
     * 若String不存在 则 返回 0
     *
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/3/8 16:10
     */
    public static BigDecimal String2Dec(String data) {
        return org.springframework.util.StringUtils.hasLength(data) ? new BigDecimal(data) : BigDecimal.ZERO;
    }

    /**
     * 电话号码校验
     *
     * @param phone
     * @author PeterChen
     * @version v1
     * @since 2019/7/8 17:14
     */
    public static boolean checkPhone(String phone) {
        return PATTERN.matcher(phone).matches() && phone.length() <= 11 && phone.length() > 0;
    }


    public static BigDecimal addStringList(List<String> stringList){
        BigDecimal result = BigDecimal.ZERO;
        if(!CollectionUtils.isEmpty(stringList)){
            for (String e : stringList) {
                if(StringUtils.isBlank(e)){
                    continue;
                }
                result = result.add(new BigDecimal(e));
            }
        }
        return result;
    }


}
