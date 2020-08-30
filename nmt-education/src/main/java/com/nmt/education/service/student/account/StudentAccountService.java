package com.nmt.education.service.student.account;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.nmt.education.service.student.account.StudentAccountPoMapper;
import java.util.List;
import com.nmt.education.pojo.po.StudentAccountPo;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 学生账户
 */
@Service
public class StudentAccountService {

    @Resource
    private StudentAccountPoMapper studentAccountPoMapper;
    @Resource
    private StudentAccountFlowPoMapper studentAccountFlowPoMapper;


    /**
     * 根据课程结转增加结余数据
     * 
     * @param logInUser
     * @param courseId
     */
    public void addByCourseFinish(Integer logInUser, Long courseId) {
    }



















    //-----------------------------------------------------------------//
    
    public int insertSelective(StudentAccountPo record) {
        return studentAccountPoMapper.insertSelective(record);
    }

    
    public StudentAccountPo selectByPrimaryKey(Long id) {
        return studentAccountPoMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(StudentAccountPo record) {
        return studentAccountPoMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateBatchSelective(List<StudentAccountPo> list) {
        return studentAccountPoMapper.updateBatchSelective(list);
    }

    
    public int batchInsert(List<StudentAccountPo> list) {
        return studentAccountPoMapper.batchInsert(list);
    }

}
