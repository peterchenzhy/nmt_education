import { GENDER, COURSE_STATUS, COURSE_SUBJECT, COURSE_TYPE, GRADE } from '@shared/constant/system.constant';

export interface Teacher {
    code?: string;
    name?: string;
    gender?: GENDER;
    contactNo?: string;
    introduction?: string;
    courseSalary?: CourseSalary[];
}

export interface CourseSalary {
    id?: string;
    grade?: GRADE;
    courseSubject?: COURSE_SUBJECT;
    courseType?: COURSE_TYPE;
    salary?: number;
}