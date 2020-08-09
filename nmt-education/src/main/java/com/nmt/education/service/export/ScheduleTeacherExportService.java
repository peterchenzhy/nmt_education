package com.nmt.education.service.export;

import com.nmt.education.pojo.dto.req.TeacherScheduleReqDto;
import com.nmt.education.pojo.vo.TeacherScheduleDto;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleTeacherExportService extends AbstractExportService<TeacherScheduleReqDto, TeacherScheduleDto> {
    @Autowired
    private CourseScheduleService courseScheduleService;

    @Override
    public List<TeacherScheduleDto> getDataList(TeacherScheduleReqDto dto, Integer logInUser) {
        List<TeacherScheduleDto> dataList = courseScheduleService.scheduleTeacherExportList(dto, logInUser);
        return dataList;
    }

    @Override
    public String getFileName() {
        return "下载课程明细统计";
    }

    @Override
    public Class getExportClass() {
        return TeacherScheduleDto.class;
    }
}


