package com.nmt.education.service.teacher;

import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.po.TeacherPo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.pojo.vo.TeacherVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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



    /**
     * 学生模糊搜索 ，左匹配 不含联系方式
     * @param name
     * @return
     */
    public List<TeacherVo> searchFuzzy(String name) {
        if(StringUtils.hasLength(name)){
            List list = this.teacherPoMapper.queryFuzzy(name);
            List<TeacherVo> result = new ArrayList<>(list.size());
            list.forEach(e->{
                TeacherVo vo = new TeacherVo();
                BeanUtils.copyProperties(e,vo);
                result.add(vo);
            });
            return result;
        }
        return Collections.emptyList();
    }
}
