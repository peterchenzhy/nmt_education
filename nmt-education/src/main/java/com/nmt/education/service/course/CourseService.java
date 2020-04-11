package com.nmt.education.service.course;

import com.nmt.education.commmons.Enums;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CourseService {

    @Autowired
    private CodeService codeService;
    @Resource
    private CoursePoMapper coursePoMapper;

    /**
     * 课程新增，修改，删除接口
     * 编辑标志；0：无变化，1：新增，2：编辑，3：需要删除
     *
     * @param loginUser
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 21:48
     */
    public Boolean courseManager(Integer loginUser, CourseReqDto dto) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 新增:
                newCourse(loginUser, dto);
                break;
            case 修改:
                editCourse(loginUser, dto);
                break;
            case 需要删除:
                editCourse(loginUser, dto);
                break;
            default:
                log.error("请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }

        return true;
    }

    /**
     * 编辑课程
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @summary
     * @since 2020/4/11 22:34
     */
    private void editCourse(Integer operator, CourseReqDto dto) {
        Assert.notNull(dto.getId(), "编辑老师缺少id");
        CoursePo coursePo = selectByPrimaryKey(dto.getId());
        Assert.notNull(coursePo, "老师信息不存在" + dto.getId());
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 需要删除:
                invalidByPrimaryKey(coursePo.getId(), operator);
                break;
            case 修改:
                BeanUtils.copyProperties(dto, coursePo);
                coursePo.setOperator(operator);
                coursePo.setOperateTime(new Date());
                this.updateByPrimaryKeySelective(coursePo);
                break;
            default:
                break;
        }
    }

    /**
     * 根据id 无效课程
     *
     * @param id
     * @param operator
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:33
     */
    private void invalidByPrimaryKey(Long id, Integer operator) {
        this.coursePoMapper.invalidByPrimaryKey(id, operator);
    }

    /**
     * 新增课程
     *
     * @param operator
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:08
     */
    private void newCourse(Integer operator, CourseReqDto dto) {
        CoursePo po = newCoursePo(operator, dto);
        this.coursePoMapper.insertSelective(po);
        //todo 课程配置
    }

    /**
     * 创建新的coursePo
     *
     * @param operator
     * @param dto
     * @return com.nmt.education.pojo.po.CoursePo
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 22:15
     */
    private CoursePo newCoursePo(Integer operator, CourseReqDto dto) {
        CoursePo po = new CoursePo();
        BeanUtils.copyProperties(dto, po);
        po.setCreator(operator);
        po.setCreateTime(new Date());
        po.setOperator(operator);
        po.setOperateTime(new Date());
        po.setCode(codeService.generateNewCourseCode(dto.getCampus(),dto.getCourseSubject()));
        return po;
    }

    public int insertSelective(CoursePo record) {
        return coursePoMapper.insertSelective(record);
    }


    public CoursePo selectByPrimaryKey(Long id) {
        return coursePoMapper.selectByPrimaryKey(id);
    }


    public int updateByPrimaryKeySelective(CoursePo record) {
        return coursePoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateBatch(List<CoursePo> list) {
        return coursePoMapper.updateBatch(list);
    }


    public int updateBatchSelective(List<CoursePo> list) {
        return coursePoMapper.updateBatchSelective(list);
    }


    public int batchInsert(List<CoursePo> list) {
        return coursePoMapper.batchInsert(list);
    }


    public int insertOrUpdate(CoursePo record) {
        return coursePoMapper.insertOrUpdate(record);
    }


    public int insertOrUpdateSelective(CoursePo record) {
        return coursePoMapper.insertOrUpdateSelective(record);
    }


}
