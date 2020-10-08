package com.nmt.education.service.teacher;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.Enums;
import com.nmt.education.commmons.StatusEnum;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.dto.req.TeacherReqDto;
import com.nmt.education.pojo.dto.req.TeacherSearchReqDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.TeacherPo;
import com.nmt.education.pojo.vo.CourseVo;
import com.nmt.education.pojo.vo.TeacherVo;
import com.nmt.education.service.campus.authorization.CampusAuthorizationService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.teacher.config.TeacherSalaryConfigService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
@Slf4j
public class TeacherService {


    @Autowired
    private TeacherSalaryConfigService teacherSalaryConfigService;
    @Autowired
    @Lazy
    private TeacherService self;
    @Resource
    private TeacherPoMapper teacherPoMapper;
    @Autowired
    @Lazy
    private CourseService courseService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;

    /**
     * 教师信息，修改，删除接口
     * 编辑标志；0：无变化，1：新增，2：编辑，3：需要删除
     *
     * @param loginUser
     * @param dto
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/4/11 21:48
     */
    public Boolean teacherManager(Integer loginUser, TeacherReqDto dto) {
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 新增:
                self.newTeacher(loginUser, dto);
                break;
            case 修改:
                self.editTeacher(loginUser, dto);
                break;
            case 需要删除:
                self.editTeacher(loginUser, dto);
                break;
            case 无变化:
                break;
            default:
                log.error("teacherService请求数据不合规，无法辨认editFlag！" + dto);
                break;
        }

        return true;
    }

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
        teacherPo.setSex(dto.getSex());
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
        Enums.EditFlag editFlag = Enums.EditFlag.codeOf(dto.getEditFlag());
        switch (editFlag) {
            case 需要删除:
                invalidByPrimaryKey(teacherPo.getId(), operator);
                break;
            case 修改:
                teacherPo.setName(dto.getName());
                teacherPo.setBirthday(dto.getBirthday());
                teacherPo.setSchool(dto.getSchool());
                teacherPo.setPhone(dto.getPhone());
                teacherPo.setSex(dto.getSex());
                teacherPo.setRemark(dto.getRemark());
                teacherPo.setOperator(operator);
                teacherPo.setOperateTime(new Date());
                this.updateByPrimaryKeySelective(teacherPo);
                break;
            default:
                break;
        }

        teacherSalaryConfigService.editSalayConfig(dto.getTeacherSalaryConfigList(), teacherPo.getId(), operator);
        return true;
    }

    public int invalidByPrimaryKey(Long id, Integer operator) {
        return teacherPoMapper.invalidByPrimaryKey(id, operator);
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
        if (DateUtil.defaultDateTime().compareTo(vo.getBirthday()) == 0) {
            vo.setBirthday(null);
        }
        //todo 老师的价格如果是敏感数据，那么这边需要做权限控制或者
        vo.setSalaryConfigList(teacherSalaryConfigService.selectByTeacherId(vo.getId()));
        return vo;
    }

    /**
     * 老师分页搜索
     *
     * @param loginUser
     * @param dto
     * @return com.github.pagehelper.PageInfo<com.nmt.education.pojo.vo.TeacherVo>
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @summary
     * @since 2020/4/11 22:50
     */
    public PageInfo<TeacherVo> search(Integer loginUser, TeacherSearchReqDto dto) {
        PageInfo<TeacherPo> pageInfo;
        if (StringUtils.hasLength(dto.getPhone())) {
            pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.queryByPhone(dto.getPhone()));
        } else {
            if (StringUtils.hasLength(dto.getName())) {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.teacherPoMapper.queryFuzzy(dto.getName()));
            } else {
                pageInfo = PageHelper.startPage(dto.getPageNo(), dto.getPageSize()).doSelectPageInfo(() -> this.queryByPhone(null));
            }
        }
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new PageInfo<>();
        }
        List<TeacherVo> voList = new ArrayList<>(pageInfo.getList().size());
        pageInfo.getList().forEach(e -> voList.add(po2vo(e)));
        PageInfo voPage = new PageInfo();
        BeanUtils.copyProperties(pageInfo, voPage);
        voPage.setList(voList);
        return voPage;
    }

    private List<TeacherPo> queryByPhone(String phone) {
        return this.teacherPoMapper.query(phone);

    }


    public TeacherVo detail(Long id) {
        Assert.notNull(id, "老师明细缺少id,id:" + id);
        TeacherPo po = selectByPrimaryKey(id);
        Assert.notNull(po, "老师明细不存在，id：" + id);
        return po2vo(po);
    }

    public PageInfo<CourseVo> courseList(int loginUser, Long teacherId, Integer pageNo, Integer pageSize) {
        CourseSearchDto searchDto = new CourseSearchDto();
        searchDto.setTeacherId(teacherId);
        searchDto.setPageNo(pageNo);
        searchDto.setPageSize(pageSize);
        return courseService.search(loginUser ,searchDto);
    }
}
