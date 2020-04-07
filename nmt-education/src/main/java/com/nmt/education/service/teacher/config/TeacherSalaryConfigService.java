package com.nmt.education.service.teacher.config;

import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.TeacherSalaryConfigReqDto;
import com.nmt.education.pojo.po.TeacherSalaryConfigPo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
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
        List<TeacherSalaryConfigReqDto> needNew = teacherSalaryConfigList.stream().filter(e -> !e.getDeleteFlg()).collect(Collectors.toList());
        List<TeacherSalaryConfigPo> list = new ArrayList<>();
        needNew.stream().forEach(e -> {
            list.add(newTeacherSalaryConfigPo(operator, e, teacherId));
        });
        invalidBatch(teacherSalaryConfigList.stream().filter(e -> Objects.nonNull(e.getId())).map(e -> e.getId()).collect(Collectors.toList()), operator);
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


}
