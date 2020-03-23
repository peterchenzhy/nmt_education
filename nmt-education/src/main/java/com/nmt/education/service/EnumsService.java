package com.nmt.education.service;

import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.IEnum;
import com.nmt.education.pojo.vo.EnumVo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EnumsService {


    /**
     * 返回全部枚举
     *
     * @return
     */
    public Map<String, List<EnumVo>> all() {
        Map<String, List<EnumVo>> map = new HashMap<>();
        map.put(getEnumKey(Enums.CourseStatus.class), getEnumVos(Enums.CourseStatus.values()));
        map.put(getEnumKey(Enums.FeeStatus.class), getEnumVos(Enums.FeeStatus.values()));
        map.put(getEnumKey(Enums.registrationStatus.class), getEnumVos(Enums.registrationStatus.values()));
        return map;
    }


    /**
     * 枚举转换EnumVo List
     *
     * @param iEnums 枚举值
     * @return List<EnumVo>
     */
    private List<EnumVo> getEnumVos(IEnum[] iEnums) {
        return Arrays.stream(iEnums).map(e -> {
            EnumVo vo = new EnumVo();
            vo.setLabel(e.getDesc());
            vo.setValue((int) e.getCode());
            vo.setTypeDesc(e.getClass().getSimpleName());
            vo.setDbConfig(false);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 生成key，传给前端的key值，首字母小写
     *
     * @param iEnumClass 枚举类
     * @return String
     */
    private String getEnumKey(Class<? extends IEnum> iEnumClass) {
        return iEnumClass.getSimpleName();
    }
}
