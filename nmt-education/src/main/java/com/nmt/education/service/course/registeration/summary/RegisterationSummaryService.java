package com.nmt.education.service.course.registeration.summary;

import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.CourseRegisterReqDto;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import com.nmt.education.service.course.registeration.summary.RegisterationSummaryPoMapper;
import com.nmt.education.pojo.po.RegisterationSummaryPo;
@Service
public class RegisterationSummaryService {

    @Resource
    private RegisterationSummaryPoMapper registerationSummaryPoMapper;


    public RegisterationSummaryPo dto2po(CourseRegisterReqDto dto, int updator, CourseRegistrationPo courseRegistrationPo, Long e) {
        RegisterationSummaryPo registerationSummaryPo = new RegisterationSummaryPo();
        registerationSummaryPo.setStudentId(dto.getStudentId());
        registerationSummaryPo.setCourseId(courseRegistrationPo.getCourseId());
        registerationSummaryPo.setCourseScheduleId(e);
        registerationSummaryPo.setCourseRegistrationId(courseRegistrationPo.getId());
        registerationSummaryPo.setSignIn(Enums.signInType.未签到.getCode());
        registerationSummaryPo.setStatus(StatusEnum.VALID.getCode());
        registerationSummaryPo.setCreator(updator);
        registerationSummaryPo.setCreateTime(new Date());
        registerationSummaryPo.setOperator(updator);
        registerationSummaryPo.setOperateTime(new Date());
        return registerationSummaryPo;
    }
    
    public int insertSelective(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.insertSelective(record);
    }

    
    public RegisterationSummaryPo selectByPrimaryKey(Long id) {
        return registerationSummaryPoMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateBatch(List<RegisterationSummaryPo> list) {
        return registerationSummaryPoMapper.updateBatch(list);
    }

    
    public int updateBatchSelective(List<RegisterationSummaryPo> list) {
        return registerationSummaryPoMapper.updateBatchSelective(list);
    }

    
    public int batchInsert(List<RegisterationSummaryPo> list) {
        return registerationSummaryPoMapper.batchInsert(list);
    }

    
    public int insertOrUpdate(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.insertOrUpdate(record);
    }

    
    public int insertOrUpdateSelective(RegisterationSummaryPo record) {
        return registerationSummaryPoMapper.insertOrUpdateSelective(record);
    }

}
