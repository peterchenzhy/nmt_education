package com.nmt.education.service;

import com.google.common.collect.Maps;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.IEnum;
import com.nmt.education.commmons.SysConfigEnum;
import com.nmt.education.pojo.po.SysConfigPo;
import com.nmt.education.pojo.vo.EnumVo;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.sysconfig.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnumsService {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private CampusAuthorizationService campusAuthorizationService;

    /**
     * 返回全部枚举
     *
     * @param logInUser
     * @param roleId
     * @return
     */
    public Map<String, List<EnumVo>> all(Integer logInUser, String roleId) {
        Map<String, List<EnumVo>> map = new HashMap<>();
        map.putAll(getLocalEnums());
        map.putAll(getDbEnums(logInUser));
        return map;
    }

    /**
     * 获取数据库枚举
     *
     * @param logInUser
     * @return
     */
    private Map<String, List<EnumVo>> getDbEnums(Integer logInUser) {
        Map<Integer, List<SysConfigPo>> sysConfigMap = sysConfigService.getAllConfigs().stream()
                //过滤校区类型
                .filter(e -> !e.getType().equals(SysConfigEnum.校区.getCode()))
                .collect(Collectors.groupingBy(SysConfigPo::getType));
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
        map.put(SysConfigEnum.校区.getDesc(), getCampus(logInUser));

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
        map.put(getEnumKey(Enums.FeeDirection.class), getEnumVos(Enums.FeeDirection.values()));
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

    /**
     * 获取有权限的校区枚举
     *
     * @param logInUser
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/18 13:07
     */
    public List<EnumVo> getCampus(Integer logInUser) {
        List<Integer> campusAuthorization = null;
        try {
            campusAuthorization = campusAuthorizationService.getCampusAuthorization(logInUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (CollectionUtils.isEmpty(campusAuthorization)) {
            return Collections.EMPTY_LIST;
        }
        List<EnumVo> list = new ArrayList<>(campusAuthorization.size());
        Map<Integer, SysConfigPo> campusMap = sysConfigService.getAllConfigs().stream()
                //过滤校区类型
                .filter(e -> e.getType().equals(SysConfigEnum.校区.getCode()))
                .collect(Collectors.toMap(k -> k.getValue(), v -> v, (v1, v2) -> v2));
        for (Integer campus : campusAuthorization) {
            SysConfigPo e = campusMap.get(campus);
            if (e == null) {
                continue;
            }
            EnumVo v = new EnumVo();
            v.setLabel(e.getDescription());
            v.setValue(e.getValue());
            v.setDbConfig(true);
            v.setType(e.getType());
            v.setTypeCode(e.getTypeCode());
            v.setTypeDesc(e.getTypeDesc());
            list.add(v);
        }
        return list;
    }
}

