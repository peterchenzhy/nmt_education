package com.nmt.education.service.statistics;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.ExpenseDetailFlowTypeEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FeeStatisticsService {

    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;

    /**
     * 分页查询接口
     *
     * @param dto
     * @param logInUser
     * @return com.github.pagehelper.PageInfo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/22 22:38
     */
    public PageInfo page(FeeStatisticsReqDto dto, Integer logInUser) {
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser);
        if(dto.getEndDate()!=null){
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        PageInfo<FeeStatisticsVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() ->
                registrationExpenseDetailService.feeStatistics(dto.getStartDate(), dto.getEndDate(), campusList,
                        ExpenseDetailFlowTypeEnum.feeStatistics2FlowType(dto.getFeeFlowType())));
        pageInfo.getList().stream().forEach(e->{
            e.setFeeFlowTypeStr(ExpenseDetailFlowTypeEnum.codeOf(e.getFeeFlowType()).getDisplay());
        });
        return pageInfo;
    }
}
