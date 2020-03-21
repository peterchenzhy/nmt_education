import { Status } from './system.model';
import { COURSE_STATUS, COURSE_SUBJECT, GRADE } from '@shared/constant/system.constant';

export interface Course {
    code?: string;
    name?: string;
    year?: number;
    season?: number;
    startDate?: Date;
    grade?: GRADE;
    subject?: COURSE_SUBJECT;
    type?: string;
    status?: COURSE_STATUS;
    statusDetail?: Status;
    assistant?: Teacher;
    campus?: Campus;
    classroom?: Classroom;
    feeList: CourseFee[];
    sessionList?: CourseSession[];
}

export interface Teacher {
    code?: string;
    name?: string;
    contactNo: string;
    salary: number;
}

export interface CourseSession {
    id?: string;
    startDateTime?: Date;
    duration?: number;
    teacher?: Teacher;
    price?: number;
    classroom?: Classroom;
}

export interface CourseFee {
    code?: string;
    name?: string;
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