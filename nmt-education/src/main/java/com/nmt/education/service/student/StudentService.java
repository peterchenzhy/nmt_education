package com.nmt.education.service.student;

import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.po.StudentPo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.service.CodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Service
public class StudentService {

    @Resource
    private StudentPoMapper studentPoMapper;

    @Autowired
    private CodeService codeService;

    /**
     * 新增学生
     *
     * @param studentReqDto
     * @return java.lang.Boolean
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @summary 新增学生
     * @since 2020/3/31 22:18
     */
    public Boolean newStudent(Integer logInUser, StudentReqDto studentReqDto) {
        StudentPo studentPo = new StudentPo();
        studentPo.setStudentCode(codeService.generateNewStudentCode(studentReqDto.getCampus()));
        studentPo.setName(studentReqDto.getName());
        studentPo.setBirthday(studentReqDto.getBirthday());
        studentPo.setSchool(studentReqDto.getSchool());
        studentPo.setGrade(studentReqDto.getGrade());
        studentPo.setPhone(studentReqDto.getPhone());
        studentPo.setSex(studentReqDto.getSex());
        studentPo.setCampus(studentReqDto.getCampus());
        studentPo.setRemark(studentReqDto.getRemark());
        studentPo.setStatus(StatusEnum.VALID.getCode());
        studentPo.setCreator(logInUser);
        studentPo.setCreateTime(new Date());
        studentPo.setOperator(logInUser);
        studentPo.setOperateTime(new Date());
        return this.insertSelective(studentPo) > 0;

    }

    /**
     * 搜索 学生
     *
     * @param logInUser
     * @param dto
     * @return  com.nmt.education.pojo.vo.StudentVo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/4 9:05
     */
    public StudentVo search(Integer logInUser, StudentSearchReqDto dto) {
        StudentPo studentPo = this.queryLike(dto.getName());
        StudentVo vo  = new StudentVo();
        BeanUtils.copyProperties(studentPo,vo);
        return vo ;
    }


    public StudentPo queryLike(String name){
        return this.studentPoMapper.queryLike(name);
    }


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
