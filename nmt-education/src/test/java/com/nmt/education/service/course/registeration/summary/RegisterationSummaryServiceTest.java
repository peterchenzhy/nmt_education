package com.nmt.education.service.course.registeration.summary;

import com.nmt.education.BaseTest;
import com.nmt.education.pojo.dto.req.RegisterSummarySearchDto;
import com.nmt.education.pojo.vo.CourseSignInItem;
import com.nmt.education.service.course.schedule.CourseScheduleService;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class RegisterationSummaryServiceTest extends BaseTest {

    @Autowired
    private RegisterationSummaryService registerationSummaryService;
    @Autowired
    private CourseScheduleService courseScheduleService;
    @Test
    void queryBySearchDto1() {
        RegisterSummarySearchDto dto = new RegisterSummarySearchDto();
        List<Integer> campusList =  Lists.newArrayList(1,2);
        System.out.println(registerationSummaryService.queryBySearchDto(dto, campusList));
    }

    @Test
    void queryByCourseScheduleId1() {
        System.out.println(registerationSummaryService.queryByCourseScheduleId(53L));
    }

    @SneakyThrows
    @Test
    void signIn1() {
        CourseSignInItem c1 = new CourseSignInItem();
        c1.setSignIn(1);
        c1.setRegisterSummaryId(19L);
        c1.setCourseId(7L);
        c1.setCourseScheduleId(53L);

        CourseSignInItem c2 = new CourseSignInItem();
        c2.setSignIn(1);
        c2.setRegisterSummaryId(18L);
        c2.setCourseScheduleId(53L);
        c2.setCourseId(7L);
        List list = Lists.newArrayList(c1,c2);

        courseScheduleService.signIn(list,333);

        Thread.sleep(5*1000L);
    }
}