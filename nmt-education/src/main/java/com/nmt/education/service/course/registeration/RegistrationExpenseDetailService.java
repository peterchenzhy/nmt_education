package com.nmt.education.service.course.registeration;

import com.google.common.base.Strings;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.RegisterExpenseDetailReqDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.RegistrationExpenseDetailPo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class RegistrationExpenseDetailService {

    @Resource
    private RegistrationExpenseDetailPoMapper registrationExpenseDetailPoMapper;


    public int insertSelective(RegistrationExpenseDetailPo record) {
        return registrationExpenseDetailPoMapper.insertSelective(record);
    }


    public RegistrationExpenseDetailPo selectByPrimaryKey(Integer id) {
        return registrationExpenseDetailPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(RegistrationExpenseDetailPo record) {
        return registrationExpenseDetailPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<RegistrationExpenseDetailPo> list) {
        return registrationExpenseDetailPoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<RegistrationExpenseDetailPo> list) {
        return registrationExpenseDetailPoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<RegistrationExpenseDetailPo> list) {
        return registrationExpenseDetailPoMapper.batchInsert(list);
    }


    public int insertOrUpdate(RegistrationExpenseDetailPo record) {
        return registrationExpenseDetailPoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(RegistrationExpenseDetailPo record) {
        return registrationExpenseDetailPoMapper.insertOrUpdateSelective(record);
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
}