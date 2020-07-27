package com.nmt.education.service.statistics;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.ExpenseDetailFlowTypeEnum;
import com.nmt.education.commmons.SysConfigEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.FeeStatisticsReqDto;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.registeration.RegistrationExpenseDetailService;
import com.nmt.education.service.sysconfig.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FeeStatisticsService {

    @Autowired
    private RegistrationExpenseDetailService registrationExpenseDetailService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private SysConfigService sysConfigService;

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
        if (dto.getEndDate() != null) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        PageInfo<FeeStatisticsVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() ->
                registrationExpenseDetailService.feeStatistics(dto.getStartDate(), dto.getEndDate(), campusList,
                        ExpenseDetailFlowTypeEnum.feeStatistics2FlowType(dto.getFeeFlowType())));
        pageInfo.getList().stream().forEach(e -> {
            e.setFeeFlowTypeStr(ExpenseDetailFlowTypeEnum.codeOf(e.getFeeFlowType()).getDisplay());
            e.setPaymentStr(Enums.PaymentType.codeOf(e.getPayment()).getDesc());
        });
        return pageInfo;
    }


    /**
     * 分页导出
     *
     * @param dto
     * @param logInUser
     * @return java.util.List<com.nmt.education.pojo.vo.FeeStatisticsVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/7/25 15:41
     */
    public List<FeeStatisticsVo> exportList(FeeStatisticsReqDto dto, Integer logInUser) {
        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(logInUser);
        if (dto.getEndDate() != null) {
            dto.setEndDate(DateUtil.parseCloseDate(dto.getEndDate()));
        }
        List<FeeStatisticsVo> resultList = new ArrayList<>(Consts.BATCH_100);
        dto.setPageNo(1);
        dto.setPageSize(Consts.BATCH_100);
        List<FeeStatisticsVo> dataList;
        do {
            dataList = getExportData(dto, campusList);
            int pageNo = dto.getPageNo() + 1;
            dto.setPageNo(pageNo);
            resultList.addAll(dataList);
        } while (!CollectionUtils.isEmpty(dataList));

        return resultList;

    }

    private List<FeeStatisticsVo> getExportData(FeeStatisticsReqDto dto, List<Integer> campusList) {
        PageInfo<FeeStatisticsVo> pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize(), false).doSelectPageInfo(() ->
                registrationExpenseDetailService.feeStatistics(dto.getStartDate(), dto.getEndDate(), campusList,
                        ExpenseDetailFlowTypeEnum.feeStatistics2FlowType(dto.getFeeFlowType())));
        pageInfo.getList().stream().forEach(e -> {
                    e.setFeeFlowTypeStr(ExpenseDetailFlowTypeEnum.codeOf(e.getFeeFlowType()).getDisplay());
                    e.setCampusStr(sysConfigService.queryByTypeValue(SysConfigEnum.校区.getCode(), e.getCampus()).getDescription());
                    e.setPaymentStr(Enums.PaymentType.codeOf(e.getPayment()).getDesc());
                }
        );
        return pageInfo.getList();
    }
}
