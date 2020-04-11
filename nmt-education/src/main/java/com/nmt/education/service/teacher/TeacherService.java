package com.nmt.education.service.teacher;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.dto.req.TeacherSearchReqDto;
import com.nmt.education.pojo.po.TeacherPo;
import com.nmt.education.pojo.vo.TeacherVo;
import com.nmt.education.service.teacher.config.TeacherSalaryConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class TeacherService {


    @Autowired
    private TeacherSalaryConfigService teacherSalaryConfigService;
    @Resource
    private TeacherPoMapper teacherPoMapper;


    /**
     * 新增老师
     *
     * @param operator 操作人
     * @param dto      请求对象
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/6 15:33
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean newTeacher(Integer operator, TeacherReqDto dto) {
        TeacherPo teacherPo = newTeacherPo(operator, dto);
        boolean result = insertSelective(teacherPo) > 0;
        teacherSalaryConfigService.newSalayConfig(dto.getTeacherSalaryConfigList(), teacherPo.getId(), operator);
        return result;
    }

    /**
     * 生成新老师po
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/7 22:08
     */
    private TeacherPo newTeacherPo(Integer operator, TeacherReqDto dto) {
        TeacherPo teacherPo = new TeacherPo();
        teacherPo.setName(dto.getName());
        teacherPo.setBirthday(dto.getBirthday());
        teacherPo.setSchool(dto.getSchool());
        teacherPo.setStatus(StatusEnum.VALID.getCode());
        teacherPo.setPhone(dto.getPhone());
        teacherPo.setRemark(dto.getRemark());
        teacherPo.setCreator(operator);
        teacherPo.setCreateTime(new Date());
        teacherPo.setOperator(operator);
        teacherPo.setOperateTime(new Date());
        return teacherPo;
    }


    /**
     * 修改老师
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/7 22:08
     */
    public Boolean editTeacher(Integer operator, TeacherReqDto dto) {
        Assert.notNull(dto.getId(), "编辑老师缺少id");
        TeacherPo teacherPo = selectByPrimaryKey(dto.getId());
        Assert.notNull(teacherPo, "老师信息不存在" + dto.getId());
        if (dto.getDeleteFlg()) {
            invalidByPrimaryKey(teacherPo.getId(), operator);
        } else {
            teacherPo.setName(dto.getName());
            teacherPo.setBirthday(dto.getBirthday());
            teacherPo.setSchool(dto.getSchool());
            teacherPo.setPhone(dto.getPhone());
            teacherPo.setRemark(dto.getRemark());
            teacherPo.setOperator(operator);
            teacherPo.setOperateTime(new Date());
            this.updateByPrimaryKeySelective(teacherPo);
        }
        teacherSalaryConfigService.editSalayConfig(dto.getTeacherSalaryConfigList(), teacherPo.getId(), operator);
        return true;
    }

    public int invalidByPrimaryKey(Long id, Integer operator) {
        return teacherPoMapper.invalidByPrimaryKey(id, operator);
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
     *
     * @param name
     * @return
     */
    public List<TeacherVo> searchFuzzy(String name) {
        if (StringUtils.hasLength(name)) {
            List<TeacherPo> list = this.teacherPoMapper.queryFuzzy(name);
            List<TeacherVo> result = new ArrayList<>(list.size());
            list.forEach(e -> {
                TeacherVo vo = po2vo(e);
                result.add(vo);
            });
            return result;
        }
        return Collections.emptyList();
    }

    private TeacherVo po2vo(TeacherPo e) {
        TeacherVo vo = new TeacherVo();
        BeanUtils.copyProperties(e, vo);
        return vo;
    }

    //老师分页搜索
    public PageInfo<TeacherVo> search(Integer logInUser, TeacherSearchReqDto dto) {
        PageInfo<TeacherPo> pageInfo;
        if (StringUtils.hasLength(dto.getPhone())) {
            pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSzie()).doSelectPageInfo(() -> this.queryByPhone(dto.getPhone()));
        } else {
            if (StringUtils.hasLength(dto.getName())) {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSzie()).doSelectPageInfo(() -> this.teacherPoMapper.queryFuzzy(dto.getName()));
            } else {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSzie()).doSelectPageInfo(() -> this.queryByPhone(null));
            }
        }
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new PageInfo<>();
        }
        List<TeacherVo> voList = new ArrayList<>(pageInfo.getList().size());
        pageInfo.getList().stream().forEach(e -> voList.add(po2vo(e)));
        PageInfo voPage = new PageInfo();
        BeanUtils.copyProperties(pageInfo, voPage);
        voPage.setList(voList);
        return voPage;
    }

    private List<TeacherPo> queryByPhone(String phone) {
        return this.teacherPoMapper.query(phone);

    }


}
