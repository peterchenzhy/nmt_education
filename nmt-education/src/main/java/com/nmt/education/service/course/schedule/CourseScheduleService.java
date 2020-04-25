package com.nmt.education.service.course.schedule;

import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.pojo.dto.req.CourseScheduleReqDto;
import com.nmt.education.pojo.po.CourseSchedulePo;
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
public class CourseScheduleService {

    @Resource
    private CourseSchedulePoMapper courseSchedulePoMapper;

    public boolean manager(List<CourseScheduleReqDto> dtoList, Long courseId, Integer operator) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return true;
        }
        dtoList.stream().forEach(e -> manager(e, courseId, operator));
        return true;
    }

    private void manager(CourseScheduleReqDto dto, Long courseId, Integer operator) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        List<CourseSchedulePo> addList = new ArrayList<>();
        List<CourseSchedulePo> editList = new ArrayList<>();
        switch (editFlag) {
            case 新增:
                addList.add(newSchedule(operator, dto,courseId));
                break;
            case 修改:
                editList.add(editSchedule(operator, dto));
                break;
            case 需要删除:
                editList.add(delSchedule(operator, dto));
                break;
            case 无变化:
                break;
            default:
                log.error("courseSchedule请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }
        updateBatchSelective(editList);
        batchInsert(addList);

    }

    private CourseSchedulePo delSchedule(Integer operator, CourseScheduleReqDto dto) {
        CourseSchedulePo po = checkExist(dto);
        po.setStatus(StatusEnum.INVALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseSchedulePo editSchedule(Integer operator, CourseScheduleReqDto dto) {
        CourseSchedulePo po = checkExist(dto);
        BeanUtils.copyProperties(dto, po);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }

    private CourseSchedulePo checkExist(CourseScheduleReqDto dto) {
        Assert.notNull(dto.getId(), "编辑课程信息缺少id");
        CourseSchedulePo po = selectByPrimaryKey(dto.getId());
        Assert.notNull(po, "课程信息不存在" + dto.getId());
        Assert.isTrue(po.getSignIn() == 1,"课表已经签到，无法进行修改或者删除,id:"+dto.getId() );
        return po;
    }

    /**
     * 新增
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/24 10:54
     */
    private CourseSchedulePo newSchedule(Integer operator, CourseScheduleReqDto dto,Long courseId) {
        CourseSchedulePo po = new CourseSchedulePo();
        BeanUtils.copyProperties(dto, po);
        po.setCourseId(courseId);
        po.setStatus(StatusEnum.VALID.getCode());
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        return po;
    }


    public int insertSelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.insertSelective(record);
    }


    public CourseSchedulePo selectByPrimaryKey(Long id) {
        return courseSchedulePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CourseSchedulePo> list) {
        return courseSchedulePoMapper.updateBatch(list);
    }


    public void updateBatchSelective(List<CourseSchedulePo> list) {
        if(CollectionUtils.isEmpty(list )){
            return ;
        }
          courseSchedulePoMapper.updateBatchSelective(list);
    }


    public void batchInsert(List<CourseSchedulePo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        courseSchedulePoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CourseSchedulePo record) {
        return courseSchedulePoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CourseSchedulePo record) {
        return courseSchedulePoMapper.insertOrUpdateSelective(record);
    }

    public List< CourseSchedulePo> queryByCourseId(Long id) {
        return this.courseSchedulePoMapper.queryByCourseId(id);
    }

    public Boolean signIn(Long id,Integer operator) {
        CourseSchedulePo po = selectByPrimaryKey(id);
        Assert.notNull(po,"课表信息为空，id："+id);
        return this.courseSchedulePoMapper.signIn(id,operator)>0;
    }
}
