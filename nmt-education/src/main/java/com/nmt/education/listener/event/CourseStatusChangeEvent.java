package com.nmt.education.listener.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CourseStatusChangeEvent extends BaseEvent{
    /**
     * 课程id
     */
    private long courseId ;

}
