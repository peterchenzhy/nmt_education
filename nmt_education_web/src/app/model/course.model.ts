import { SysEnum } from './system.model';
import { COURSE_STATUS, COURSE_TYPE } from '@shared/constant/system.constant';
import { Teacher } from './teacher.model';

export interface Course {
    code?: string;
    name?: string;
    year?: number;
    season?: number;
    startDate?: Date;
    grade?: number;
    subject?: number;
    type?: COURSE_TYPE;
    status?: COURSE_STATUS;
    statusDetail?: SysEnum;
    assistant?: Teacher;
    campus?: number;
    classroom?: Classroom;
    feeList?: CourseFee[];
    sessionList?: CourseSession[];
}


export interface CourseSession {
    id?: string;
    startDateTime?: Date;
    duration?: number;
    teacher?: string;
    price?: number
}

export interface CourseFee {
    code?: string;
    type?: number;
    price?: number;
}

export interface Campus {
    code?: string;
    name?: string;
    address?: string;
    classrooms?: Classroom[];
}

export interface Classroom {
    code?: string;
    name?: string;
}