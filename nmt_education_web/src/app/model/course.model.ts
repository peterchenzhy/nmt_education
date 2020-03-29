import { Status } from './system.model';
import { COURSE_STATUS, COURSE_SUBJECT, GRADE, COURSE_TYPE, FEE_TYPE, CAMPUS, SEASON } from '@shared/constant/system.constant';
import { Teacher } from './teacher.model';

export interface Course {
    code?: string;
    name?: string;
    year?: number;
    season?: SEASON;
    startDate?: Date;
    grade?: GRADE;
    subject?: COURSE_SUBJECT;
    type?: COURSE_TYPE;
    status?: COURSE_STATUS;
    statusDetail?: Status;
    assistant?: Teacher;
    campus?: CAMPUS;
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
    type?: FEE_TYPE;
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