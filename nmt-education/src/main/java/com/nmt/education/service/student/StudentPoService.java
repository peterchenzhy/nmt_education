package com.nmt.education.service.student;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.nmt.education.pojo.po.StudentPo;
import com.nmt.education.service.student.StudentPoMapper;
/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Service
public class StudentPoService{

    @Resource
    private StudentPoMapper studentPoMapper;

    
    public int deleteByPrimaryKey(Long id) {
        return studentPoMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(StudentPo record) {
        return studentPoMapper.insert(record);
    }

    
    public int insertSelective(StudentPo record) {
        return studentPoMapper.insertSelective(record);
    }

    
    public StudentPo selectByPrimaryKey(Long id) {
        return studentPoMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(StudentPo record) {
        return studentPoMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateByPrimaryKey(StudentPo record) {
        return studentPoMapper.updateByPrimaryKey(record);
    }

}
