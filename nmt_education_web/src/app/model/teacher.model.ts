import { GENDER, COURSE_STATUS, COURSE_SUBJECT, COURSE_TYPE, GRADE } from '@shared/constant/system.constant';

export interface Teacher {
    code?: string;
    name?: string;
    gender?: GENDER;
    contactNo?: string;
    courseSubject?: COURSE_SUBJECT;
    courseSalary?: CourseSalary[];
}

export interface CourseSalary {
    id?: string;
    grade?: GRADE;
    courseType?: COURSE_TYPE;
    salary?: number;
}