package com.nmt.education.service.teacher.config;

import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.TeacherSalaryConfigReqDto;
import com.nmt.education.pojo.po.TeacherSalaryConfigPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeacherSalaryConfigService {

    @Resource
    private TeacherSalaryConfigPoMapper teacherSalaryConfigPoMapper;

    /**
     * 批量插入
     *
     * @param salaryConfigPoList
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/6 14:52
     */
    private void insertBatch(List<TeacherSalaryConfigPo> salaryConfigPoList) {
        if (CollectionUtils.isEmpty(salaryConfigPoList)) {
            return;
        }
        this.teacherSalaryConfigPoMapper.batchInsert(salaryConfigPoList);
    }

    /**
     * 根据id 批量无效
     *
     * @param ids
     * @param operator
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/6 14:53
     */
    private void invalidBatch(List<Long> ids, Integer operator) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.teacherSalaryConfigPoMapper.invalidBatchByids(ids, operator, new Date());
    }


    /**
     * 批量新增 教师薪资配置
     *
     * @param teacherSalaryConfigList
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/6 15:35
     */
    public void newSalayConfig(List<TeacherSalaryConfigReqDto> teacherSalaryConfigList, long teacherId, int operator) {
        if (CollectionUtils.isEmpty(teacherSalaryConfigList)) {
            return;
        }
        List<TeacherSalaryConfigPo> list = new ArrayList<>(teacherSalaryConfigList.size());
        teacherSalaryConfigList.forEach(e -> {
            TeacherSalaryConfigPo po = newTeacherSalaryConfigPo(operator, e, teacherId);
            list.add(po);
        });
        insertBatch(list);

    }

    /**
     * 编辑薪资配置
     *
     * @param teacherSalaryConfigList
     * @param teacherId
     * @param operator
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/7 22:28
     */
    public void editSalayConfig(List<TeacherSalaryConfigReqDto> teacherSalaryConfigList, Long teacherId, Integer operator) {
        if (CollectionUtils.isEmpty(teacherSalaryConfigList)) {
            return;
        }
        List<TeacherSalaryConfigReqDto> del = teacherSalaryConfigList.stream().filter(e -> Enums.EditFlag.需要删除.getCode().equals(e.getEditFlag())
        ).collect(Collectors.toList());
        List<TeacherSalaryConfigReqDto> needNew = teacherSalaryConfigList.stream().filter(e -> Enums.EditFlag.新增.getCode().equals(e.getEditFlag())
                || Enums.EditFlag.修改.getCode().equals(e.getEditFlag())
        ).collect(Collectors.toList());
        List<TeacherSalaryConfigPo> list = new ArrayList<>();
        needNew.stream().forEach(e -> {
            list.add(newTeacherSalaryConfigPo(operator, e, teacherId));
        });
        needNew.addAll(del);
        invalidBatch(needNew.stream().filter(e -> Objects.nonNull(e.getId())).map(e -> e.getId()).collect(Collectors.toList()), operator);
        insertBatch(list);
    }

    private TeacherSalaryConfigPo newTeacherSalaryConfigPo(int operator, TeacherSalaryConfigReqDto e, Long teacherId) {
        TeacherSalaryConfigPo po = new TeacherSalaryConfigPo();
        BeanUtils.copyProperties(e, po);
        po.setTeacherId(teacherId);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    public int deleteByPrimaryKey(Integer id) {
        return teacherSalaryConfigPoMapper.deleteByPrimaryKey(id);
    }


    public int insert(TeacherSalaryConfigPo record) {
        return teacherSalaryConfigPoMapper.insert(record);
    }


    public int insertSelective(TeacherSalaryConfigPo record) {
        return teacherSalaryConfigPoMapper.insertSelective(record);
    }


    public TeacherSalaryConfigPo selectByPrimaryKey(Integer id) {
        return teacherSalaryConfigPoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(TeacherSalaryConfigPo record) {
        return teacherSalaryConfigPoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateByPrimaryKey(TeacherSalaryConfigPo record) {
        return teacherSalaryConfigPoMapper.updateByPrimaryKey(record);
    }


    /**
     * 根据老师id 获取价格信息
     *
     * @param teacherId
     * @return java.util.List<com.nmt.education.pojo.po.TeacherSalaryConfigPo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:57
     */
    public List<TeacherSalaryConfigPo> selectByTeacherId(Long teacherId) {
        if(Objects.isNull(teacherId)){
            log.error("selectByTeacherId param--teacherId 为空");
            return Collections.EMPTY_LIST;
        }
        return this.teacherSalaryConfigPoMapper.selectByTeacherId(teacherId);
    }
}
