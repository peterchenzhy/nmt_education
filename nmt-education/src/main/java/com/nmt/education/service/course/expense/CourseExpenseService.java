package com.nmt.education.service.course.expense;

import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.CourseExpenseReqDto;
import com.nmt.education.pojo.po.CourseExpensePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CourseExpenseService {

    @Resource
    private CourseExpensePoMapper courseExpensePoMapper;

    public Boolean manager(List<CourseExpenseReqDto> dtoList, Long courseId, Integer operator) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return true ;
        }
        dtoList.stream().forEach(e -> manager(e, courseId, operator));
        return true;
    }


    private void manager(CourseExpenseReqDto dto, Long courseId, Integer operator) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        List<CourseExpensePo> addList = new ArrayList<>();
        List<CourseExpensePo> editList = new ArrayList<>();
        switch (editFlag) {
            case 新增:
                addList.add(newExpense(operator, dto,courseId));
                break;
            case 修改:
                editList.add(editExpense(operator, dto));
                break;
            case 需要删除:
                editList.add(delExpense(operator, dto));
                break;
            case 无变化:
                break;
            default:
                log.error("courseExpense请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }
        updateBatchSelective(editList);
        batchInsert(addList);
    }

    private CourseExpensePo newExpense(Integer operator, CourseExpenseReqDto dto,Long courseId) {
        CourseExpensePo po = new CourseExpensePo();
        BeanUtils.copyProperties(dto, po);
        po.setCourseId(courseId);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseExpensePo delExpense(Integer operator, CourseExpenseReqDto dto) {
        CourseExpensePo po = checkExist(dto);
        po.setStatus(StatusEnum.INVALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseExpensePo editExpense(Integer operator, CourseExpenseReqDto dto) {
        CourseExpensePo po = checkExist(dto);
        BeanUtils.copyProperties(dto, po);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseExpensePo checkExist(CourseExpenseReqDto dto) {
        Assert.notNull(dto.getId(), "编辑课程信息缺少id");
        CourseExpensePo po = selectByPrimaryKey(dto.getId());
        Assert.notNull(po, "课程信息不存在" + dto.getId());
        return po;
    }


    public int insertSelective(CourseExpensePo record) {
        return courseExpensePoMapper.insertSelective(record);
    }


    public CourseExpensePo selectByPrimaryKey(Long id) {
        return courseExpensePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CourseExpensePo record) {
        return courseExpensePoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CourseExpensePo> list) {
        return courseExpensePoMapper.updateBatch(list);
    }


    public void updateBatchSelective(List<CourseExpensePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        courseExpensePoMapper.updateBatchSelective(list);
    }


    public void batchInsert(List<CourseExpensePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        courseExpensePoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CourseExpensePo record) {
        return courseExpensePoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CourseExpensePo record) {
        return courseExpensePoMapper.insertOrUpdateSelective(record);
    }


    public List<CourseExpensePo> queryByCourseId(Long id) {
        return this.courseExpensePoMapper.queryByCourseId(id);
    }
}
