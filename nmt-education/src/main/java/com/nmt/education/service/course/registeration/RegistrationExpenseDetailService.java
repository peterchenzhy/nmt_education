package com.nmt.education.service.course.registeration;

import com.google.common.base.Strings;
import com.nmt.education.commmons.ExpenseDetailFlowTypeEnum;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.RegisterExpenseDetailReqDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailFlowPo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import com.nmt.education.pojo.vo.ExpenseDetailFlowVo;
import com.nmt.education.pojo.vo.FeeStatisticsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RegistrationExpenseDetailService {

    @Resource
    private RegistrationExpenseDetailPoMapper registrationExpenseDetailPoMapper;
    @Resource
    private RegistrationExpenseDetailFlowMapper registrationExpenseDetailFlowMapper;


    public List<ExpenseDetailFlowVo> getExpenseDetailFlowVo(Long registerId) {
        if (Objects.isNull(registerId)) {
            return Collections.EMPTY_LIST;
        }
        List<RegistrationExpenseDetailFlowPo> poList = registrationExpenseDetailFlowMapper.queryByRegisterId(registerId);
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.EMPTY_LIST;
        }
        List<ExpenseDetailFlowVo> resultList = new ArrayList<>(poList.size());
        poList.stream().forEach(po -> {
            ExpenseDetailFlowVo vo = new ExpenseDetailFlowVo();
            BeanUtils.copyProperties(po, vo);
            vo.setType(ExpenseDetailFlowTypeEnum.code2Display(po.getType()));
            vo.setOperateTime(po.getCreateTime());
            resultList.add(vo);
        });
        return resultList;
    }


    public void batchInsertFlow(List<RegistrationExpenseDetailFlowPo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        registrationExpenseDetailFlowMapper.batchInsert(list);
    }


    public RegistrationExpenseDetailPo selectByPrimaryKey(Long id) {
        return registrationExpenseDetailPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(RegistrationExpenseDetailPo record) {
        return registrationExpenseDetailPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<RegistrationExpenseDetailPo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return registrationExpenseDetailPoMapper.updateBatch(list);
    }


    public int batchInsert(List<RegistrationExpenseDetailPo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return registrationExpenseDetailPoMapper.batchInsert(list);
    }


    public RegistrationExpenseDetailPo dto2po(int updator, CourseRegistrationPo courseRegistrationPo, RegisterExpenseDetailReqDto e) {
        RegistrationExpenseDetailPo detailPo = new RegistrationExpenseDetailPo();
        detailPo.setRegistrationId(courseRegistrationPo.getId());
        detailPo.setFeeType(e.getFeeType());
        detailPo.setFeeStatus(e.getFeeStatus());
        detailPo.setAmount(e.getAmount());
        detailPo.setPerAmount(e.getPerAmount());
        detailPo.setCount(e.getCount());
        detailPo.setDiscount(e.getDiscount());
        detailPo.setPayment(e.getPayment());
        detailPo.setStatus(StatusEnum.VALID.getCode());
        detailPo.setRemark(Strings.nullToEmpty(e.getRemark()));
        detailPo.setCreator(updator);
        detailPo.setCreateTime(new Date());
        detailPo.setOperator(updator);
        detailPo.setOperateTime(new Date());
        return detailPo;
    }

    public List<RegistrationExpenseDetailPo> queryRegisterId(Long registerId) {
        return this.registrationExpenseDetailPoMapper.queryRegisterId(registerId);
    }

    public List<RegistrationExpenseDetailPo> selectByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.registrationExpenseDetailPoMapper.selectByIds(ids);
    }

    //费用统计页面
    public List<FeeStatisticsVo> feeStatistics(Date startDate, Date endDate, List<Integer> canpusList, List<Integer> type) {
        return registrationExpenseDetailFlowMapper.feeStatistics(startDate, endDate, canpusList, type);
    }
}
