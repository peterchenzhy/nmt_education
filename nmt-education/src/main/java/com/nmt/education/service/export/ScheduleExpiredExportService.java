package com.nmt.education.service.export;

import com.google.common.collect.Lists;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.NumberUtil;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import com.nmt.education.pojo.vo.RegisterSummaryVo;
import com.nmt.education.pojo.vo.ScheduleSignInSummaryDto;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.sysconfig.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleExpiredExportService extends AbstractExportService<RegisterSummarySearchDto, ScheduleSignInSummaryDto> {
    @Autowired
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;
    @Autowired
    private SysConfigService configService;

    @Override
    public List<ScheduleSignInSummaryDto> getDataList(RegisterSummarySearchDto dto, Integer logInUser) {
        if (Objects.nonNull(dto.getEndDate())) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        if (Objects.nonNull(dto.getRegisterEndDate())) {
            dto.setRegisterEndDate(DateUtil.parseCloseDate(dto.getRegisterEndDate()));
        }
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser, dto.getCampus());

        List<RegisterSummaryVo> list = courseRegistrationService.queryBySearchDto(dto, campusList);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        Map<Integer, String> campusMap = configService.getTypeMap(Consts.CONFIG_TYPE_校区);
        Map<Integer, String> courseSubjectMap = configService.getTypeMap(Consts.CONFIG_TYPE_科目);
        Map<Integer, String> gradeMap = configService.getTypeMap(Consts.CONFIG_TYPE_年级);
        Map<Integer, String> seasonMap = configService.getTypeMap(Consts.CONFIG_TYPE_季节);

        List<ScheduleSignInSummaryDto> dtoList = new ArrayList<>(list.size());
        List<List<RegisterSummaryVo>> lists = Lists.partition(list, 10);
        lists.forEach(lst -> {
            Map<Long, RegistrationExpenseDetailPo> registrationExpenseDetailPoMap =
                    registrationExpenseDetailService.queryRegisterIds(lst.stream().map(CourseRegistrationPo::getCourseRegistrationId).collect(Collectors.toList()))
                            .stream().filter(e -> Consts.FEE_TYPE_普通单节费用.equals(e.getFeeType()))
                            .collect(Collectors.toMap(RegistrationExpenseDetailPo::getRegistrationId, v -> v));

            lst.forEach(vo -> {
                ScheduleSignInSummaryDto dt = new ScheduleSignInSummaryDto();
                BeanUtils.copyProperties(vo, dt);
                RegistrationExpenseDetailPo expenseDetailPo = registrationExpenseDetailPoMap.get(dt.getCourseRegistrationId());
                dt.setPrice(NumberUtil.String2Dec(expenseDetailPo.getDiscount())
                        .multiply(NumberUtil.String2Dec(expenseDetailPo.getPerAmount()))
                        .toPlainString());
                dt.setCampusStr(campusMap.get(dt.getCampus()));
                dt.setSubject(courseSubjectMap.get(dt.getCourseSubject()));
                dt.setGradeStr(gradeMap.get(dt.getGrade()));
                dt.setSignInStr(Enums.SignInType.code2Desc(dt.getSignIn()));

                dt.setSeasonStr(seasonMap.get(dt.getSeason()) );
                dtoList.add(dt);
            });
        });


        return dtoList;
    }

    @Override
    public String getFileName() {
        return "签到统计报表";
    }

    @Override
    public Class getExportClass() {
        return ScheduleSignInSummaryDto.class;
    }
}


