package com.nmt.education.listener.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TeacherChangeEvent {
    /**
     * 课程id
     */
    private long courseId ;
    /**
     * 老师id
     */
    private long newTeacherId;

}
