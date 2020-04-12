package com.nmt.education.service.student;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.StudentReqDto;
import com.nmt.education.pojo.dto.req.StudentSearchReqDto;
import com.nmt.education.pojo.po.StudentPo;
import com.nmt.education.pojo.vo.StudentVo;
import com.nmt.education.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: PeterChen
 * @Date: 2020/3/19 0:29
 * @Version 1.0
 */
@Service
@Slf4j
public class StudentService {

    @Resource
    private StudentPoMapper studentPoMapper;

    @Autowired
    private CodeService codeService;

    @Autowired
    @Lazy
    private StudentService self;


    public Boolean studentManager(Integer operator, StudentReqDto dto) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 新增:
                self.newStudent(operator, dto);
                break;
            case 修改:
                self.editStudent(operator, dto);
                break;
            case 需要删除:
                self.delStudent(operator, dto);
                break;
            default:
                log.error("请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }

        return true;
    }

    private void delStudent(Integer operator, StudentReqDto dto) {
        invalidByPrimaryKey(operator,dto.getId());
    }

    private void invalidByPrimaryKey(Integer operator, Long id) {
        if(Objects.isNull(id)){
            log.error("invalidByPrimaryKey id is null");
            return ;
        }
        this.studentPoMapper.invalidByPrimaryKey(operator,id);
        
    }


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
    public Boolean newStudent(Integer operator, StudentReqDto studentReqDto) {
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
        studentPo.setCreator(operator);
        studentPo.setCreateTime(new Date());
        studentPo.setOperator(operator);
        studentPo.setOperateTime(new Date());
        return this.insertSelective(studentPo) > 0;
    }


    /**
     * 编辑学生
     *
     * @param operator
     * @param dto
     * @return java.lang.Boolean
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/5 19:59
     */
    public Boolean editStudent(Integer operator, StudentReqDto dto) {
        Assert.notNull(dto.getId(),"编辑学生缺少id");
        StudentPo studentPo = selectByPrimaryKey(dto.getId());
        Assert.notNull(studentPo,"学生信息不存在"+dto.getId());
        studentPo.setName(dto.getName());
        studentPo.setBirthday(dto.getBirthday());
        studentPo.setSchool(dto.getSchool());
        studentPo.setGrade(dto.getGrade());
        studentPo.setPhone(dto.getPhone());
        studentPo.setSex(dto.getSex());
        studentPo.setCampus(dto.getCampus());
        studentPo.setRemark(dto.getRemark());
        studentPo.setOperator(operator);
        studentPo.setOperateTime(new Date());
        return this.updateByPrimaryKeySelective(studentPo)>0 ;
    }

    /**
     * 搜索 学生
     *
     * @param operator
     * @param dto
     * @return com.nmt.education.pojo.vo.StudentVo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/4 9:05
     */
    public PageInfo<StudentVo> search(Integer operator, StudentSearchReqDto dto) {
        PageInfo<StudentPo> pageInfo;
        if (StringUtils.hasLength(dto.getPhone())) {
            pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.queryByPhone(dto.getPhone()));
        } else {
            if (StringUtils.hasLength(dto.getName())) {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.searchFuzzy(dto.getName()));
            } else {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.queryByPhone(null));
            }
        }
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new PageInfo<>();
        }
        List<StudentVo> voList = new ArrayList<>(pageInfo.getList().size());
        pageInfo.getList().stream().forEach(e -> voList.add(po2vo(e)));
        PageInfo voPage = new PageInfo();
        BeanUtils.copyProperties(pageInfo, voPage);
        voPage.setList(voList);
        return voPage;
    }

    private List<StudentPo> queryByPhone(String phone) {
        return this.studentPoMapper.query(phone);
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




    /**
     * 学生模糊搜索 ，左匹配 不含联系方式
     *
     * @param name
     * @return
     */
    public List<StudentVo> searchFuzzy(String name) {
        if (StringUtils.hasLength(name)) {
            List<StudentPo> list = this.studentPoMapper.queryFuzzy(name);
            List<StudentVo> result = new ArrayList<>(list.size());
            list.forEach(e -> {
                StudentVo vo = po2vo(e);
                result.add(vo);
            });
            return result;
        }
        return Collections.emptyList();
    }

    private StudentVo po2vo(StudentPo po) {
        StudentVo vo = new StudentVo();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }



}
