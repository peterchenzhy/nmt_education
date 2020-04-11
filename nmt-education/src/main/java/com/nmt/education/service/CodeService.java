package com.nmt.education.service;

import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.SysConfigEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.po.SysConfigPo;
import com.nmt.education.service.sysconfig.SysConfigService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.nmt.education.commmons.Consts.学生编号长度;
import static com.nmt.education.commmons.Consts.课程编号长度;

@Service
public class CodeService {

    @Autowired
    private SysConfigService sysConfigService;


    /**
     * 给新增学生 生成学生编号
     *
     * @param campus
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:35
     */
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

    /**
     * 给新增课程 生成课程编号
     *
     * @param campus
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:35
     */
    public String generateNewCourseCode(Integer campus, Integer courseSubject) {
        List<String> strList = new ArrayList<>();
        strList.add(getPrefixByCampusCode(campus));
        strList.add(DateUtil.formatDate2(new Date()));
        strList.add(getPrefixByCampusCode(courseSubject));
        strList.add(RandomStringUtils.random(7, true, true));

        String result = String.join("", strList);
        if (result.length() > 课程编号长度) {
            return result.substring(0, 课程编号长度 - 1);
        } else {
            return result;
        }
    }

    /**
     * 获取 课程科目remark
     *
     * @param courseSubject
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:41
     */
    private String getPrefixByCourseSubjectCode(Integer courseSubject) {
        if (Objects.isNull(courseSubject)) {
            return Strings.EMPTY;
        }
        SysConfigPo result = sysConfigService.queryByTypeValue(SysConfigEnum.课程科目.getCode(), courseSubject);
        return Objects.nonNull(result) ? result.getRemark() : Strings.EMPTY;
    }


    /**
     * 获取 校区remark
     *
     * @param campus
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:41
     */
    private String getPrefixByCampusCode(Integer campus) {
        if (Objects.isNull(campus)) {
            return Consts.X校区;
        }
        SysConfigPo result = sysConfigService.queryByTypeValue(SysConfigEnum.校区.getCode(), campus);
        return Objects.nonNull(result) ? result.getRemark() : Consts.X校区;
    }
}
