package com.nmt.education.listener;


import com.nmt.education.listener.event.TeacherChangeEvent;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyApplicationListener {

    @Autowired
    private CourseScheduleService courseScheduleService;

    @EventListener
    public void event(TeacherChangeEvent event) {
        log.info("收到TeacherChangeEvent：" + event);
        courseScheduleService.changeTeacher(event.getCourseId(), event.getNewTeacherId());
        log.info("处理完毕TeacherChangeEvent：" + event);
    }
}
