package com.nmt.education.listener.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TeacherChangeEvent  extends BaseEvent{
    /**
     * 课程id
     */
    private long courseId ;
    /**
     * 老师id
     */
    private long newTeacherId;

}
