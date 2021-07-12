package com.nmt.education.service.export.signInTable;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.utils.DateUtil;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.po.CourseRegistrationPo;
import com.nmt.education.pojo.po.CourseSchedulePo;
import com.nmt.education.pojo.po.StudentPo;
import com.nmt.education.service.authorization.AuthorizationCheckDto;
import com.nmt.education.service.authorization.AuthorizationDto;
import com.nmt.education.service.authorization.AuthorizationService;
import com.nmt.education.service.authorization.campus.CampusAuthorizationService;
import com.nmt.education.service.course.CourseService;
import com.nmt.education.service.course.registeration.CourseRegistrationService;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import com.nmt.education.service.student.StudentService;
import com.nmt.education.service.sysconfig.SysConfigService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SignInTableExportService {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CampusAuthorizationService campusAuthorizationService;
    @Autowired
    private CourseRegistrationService courseRegistrationService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private CourseScheduleService courseScheduleService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AuthorizationService authorizationService;


    private final static String 课程签到表 = "课程签到表";
    private int DEFAULT_TIMES = 22;
    //5年级新概念英语2 班（2020秋季班)
    private final static String HEAD1 = "%s ( %s )";
    //开课日期：2020年9月5日         上课时间：18:30-20:30             课时：共 17  次
    private final static String HEAD2 = "开课日期：%s                          上课时间：%s                          课时：共 %s 次";


    public void doExport(CourseSearchDto dto, Integer loginUserId, HttpServletResponse response) throws IOException {
        AuthorizationCheckDto authorizationCheckDto = new AuthorizationCheckDto();
        authorizationCheckDto.setUserId(loginUserId);
        authorizationCheckDto.setCampus(dto.getCampus());
        authorizationCheckDto.setGrade(dto.getGrade());
        AuthorizationDto authorization = authorizationService.getAuthorization(authorizationCheckDto);
//        List<Integer> campusList = campusAuthorizationService.getCampusAuthorization(loginUserId, dto.getCampus());
        Assert.isTrue(!CollectionUtils.isEmpty(authorization.getCampusList()), "没有任何校区权限进行课程搜索");
        Assert.isTrue(!CollectionUtils.isEmpty(authorization.getGradeList()), "没有任何年级权限进行课程搜索");
        List<CoursePo> coursePos = courseService.getCoursePos(dto,authorization.getCampusList(),authorization.getGradeList());

        //设置response
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + "课程签到表" + ".xlsx");

        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();

        coursePos.forEach(coursePo -> {
            //课程时间
            List<CourseSchedulePo> courseSchedulePos = courseScheduleService.queryByCourseId(coursePo.getId());
            courseSchedulePos.sort(Comparator.comparing(CourseSchedulePo::getCourseTimes));
            int columns = courseSchedulePos.size() <= DEFAULT_TIMES ? DEFAULT_TIMES : courseSchedulePos.size();
            List<String> times = new ArrayList<>();
            List<String> date = new ArrayList<>();
            courseSchedulePos.forEach(courseSchedulePo -> {
                times.add(String.valueOf(courseSchedulePo.getCourseTimes()));
                date.add(DateUtil.mmdd(courseSchedulePo.getCourseDatetime()));
            });

            List<CourseRegistrationPo> courseRegistrationPos = courseRegistrationService.queryByCourseId(coursePo.getId());
            List<TableData> dataList = new ArrayList<>();
            Map<Long, StudentPo> studentPoMap =
                    studentService.queryByIds(courseRegistrationPos.stream().map(CourseRegistrationPo::getStudentId).collect(Collectors.toList())).stream()
                            .collect(Collectors.toMap(StudentPo::getId, v -> v));
            int count = 1;
            for (CourseRegistrationPo courseRegistrationPo : courseRegistrationPos) {
                dataList.add(new TableData(String.valueOf(count), studentPoMap.get(courseRegistrationPo.getStudentId()).getName()));
                count++;
            }

            List<TableHead1> tableHead1s =
                    Lists.newArrayList(new TableHead1(String.format(HEAD1, coursePo.getName(),
                            coursePo.getYear() + "年"+configService.queryByTypeValue(Consts.CONFIG_TYPE_季节, coursePo.getSeason()).getDescription())),
                            new TableHead1(String.format(HEAD2, DateUtil.formatDate(coursePo.getStartDate()), coursePo.getCourseRegular(),
                                    coursePo.getTimes())));
            int maxRow = dataList.size() <= DEFAULT_TIMES ? DEFAULT_TIMES : dataList.size();
            for (int i = 1; i <= maxRow; i++) {
                tableHead1s.add(new TableHead1(""));
            }


            WriteSheet writeSheet = new ExcelWriterSheetBuilder(excelWriter)
                    .sheetName(coursePo.getName().replace("/", "_")).needHead(false)
                    .registerWriteHandler(new Row1Row2WriterHandler(columns))
                    .registerWriteHandler(new Row3Row4WriterHandler(columns, times, date))
                    .registerWriteHandler(new DataRowWriterHandler(columns, dataList))
                    .build();

            excelWriter.write(tableHead1s, writeSheet);

        });

        excelWriter.finish();
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class TableHead1 {
        private String head;
    }

}



