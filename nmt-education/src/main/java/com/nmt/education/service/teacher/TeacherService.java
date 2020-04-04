package com.nmt.education.service.teacher;

import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.po.TeacherPo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class TeacherService {

    @Resource
    private TeacherPoMapper teacherPoMapper;


    //新增老师
    public Boolean newTeacher(Integer logInUser, TeacherReqDto dto) {
        TeacherPo teacherPo = new TeacherPo();
        teacherPo.setName(dto.getName());
        teacherPo.setBirthday(dto.getBirthday());
        teacherPo.setSchool(dto.getSchool());
        teacherPo.setStatus(StatusEnum.VALID.getCode());
        teacherPo.setRemark(dto.getRemark());
        teacherPo.setCreator(logInUser);
        teacherPo.setCreateTime(new Date());
        teacherPo.setOperator(logInUser);
        teacherPo.setOperateTime(new Date());
        return insertSelective(teacherPo) > 0;

    }

    public int deleteByPrimaryKey(Long id) {
        return teacherPoMapper.deleteByPrimaryKey(id);
    }


    public int insert(TeacherPo record) {
        return teacherPoMapper.insert(record);
    }


    public int insertSelective(TeacherPo record) {
        return teacherPoMapper.insertSelective(record);
    }


    public TeacherPo selectByPrimaryKey(Long id) {
        return teacherPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(TeacherPo record) {
        return teacherPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateByPrimaryKey(TeacherPo record) {
        return teacherPoMapper.updateByPrimaryKey(record);
    }


}
