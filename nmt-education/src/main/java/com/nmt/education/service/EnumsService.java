package com.nmt.education.service;

import com.google.common.collect.Maps;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.IEnum;
import com.nmt.education.commmons.SysConfigEnum;
import com.nmt.education.pojo.po.SysConfigPo;
import com.nmt.education.pojo.vo.EnumVo;
import com.nmt.education.service.sysconfig.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnumsService {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 返回全部枚举
     *
     * @return
     */
    public Map<String, List<EnumVo>> all() {
        Map<String, List<EnumVo>> map = new HashMap<>();
        map.putAll(getLocalEnums());
        map.putAll(getDbEnums());
        return map;
    }

    /**
     * 获取数据库枚举
     *
     * @return
     */
    private Map<String, List<EnumVo>> getDbEnums() {
        Map<Integer, List<SysConfigPo>> sysConfigMap = sysConfigService.getAllConfigs().stream().collect(Collectors.groupingBy(SysConfigPo::getType));
        if (CollectionUtils.isEmpty(sysConfigMap)) {
            return Maps.newHashMap();
        }
        Map<String, List<EnumVo>> map = new HashMap<>();
        sysConfigMap.keySet().stream().forEach(k -> {
            List<EnumVo> l = new ArrayList<>();
            sysConfigMap.get(k).stream().forEach(e -> {
                EnumVo v = new EnumVo();
                v.setLabel(e.getDescription());
                v.setValue(e.getValue());
                v.setDbConfig(true);
                v.setType(e.getType());
                v.setTypeCode(e.getTypeCode());
                v.setTypeDesc(e.getTypeDesc());
                l.add(v);
            });
            map.put(SysConfigEnum.codeOf(k).getDesc(), l);
        });


        return map;
    }

    /**
     * 获取本地枚举
     *
     * @return
     */
    private Map<String, List<EnumVo>> getLocalEnums() {
        Map<String, List<EnumVo>> map = new HashMap<>();
        map.put(getEnumKey(Enums.CourseStatus.class), getEnumVos(Enums.CourseStatus.values()));
        map.put(getEnumKey(Enums.FeeStatus.class), getEnumVos(Enums.FeeStatus.values()));
        map.put(getEnumKey(Enums.RegistrationStatus.class), getEnumVos(Enums.RegistrationStatus.values()));
        map.put(getEnumKey(Enums.EditFlag.class), getEnumVos(Enums.EditFlag.values()));
        map.put(getEnumKey(Enums.RegistrationType.class), getEnumVos(Enums.RegistrationType.values()));
        map.put(getEnumKey(Enums.SignInType.class), getEnumVos(Enums.SignInType.values()));
        map.putAll(getDbEnums());
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
            vo.setIcon(e.getIcon());
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
        String s = iEnumClass.getSimpleName();
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
