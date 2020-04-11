package com.nmt.education.service;

import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.SysConfigEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.po.SysConfigPo;
import com.nmt.education.service.sysconfig.SysConfigService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.nmt.education.commmons.Consts.学生编号长度;

@Service
public class CodeService {

    @Autowired
    private SysConfigService sysConfigService;


    //新增学生
    public String generateNewStudentCode(Integer campus) {
        List<String> strList = new ArrayList<>();
        strList.add(getPrefixByCampusCode(campus));
        strList.add(DateUtil.formatDate2(new Date()));
        strList.add(RandomStringUtils.random(4, true, true));

        String result = String.join("", strList);
        if (result.length() > 学生编号长度) {
            return result.substring(0, 学生编号长度 - 1);
        } else {
            return result;
        }
    }


    private String getPrefixByCampusCode(Integer campus) {
        if (Objects.isNull(campus)) {
            return Consts.X校区;
        }
        SysConfigPo result = sysConfigService.queryByTypeValue(SysConfigEnum.校区.getCode(), campus);
        return Objects.nonNull(result) ? result.getRemark() : Consts.X校区;

    }
}
