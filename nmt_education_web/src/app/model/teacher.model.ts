import { GENDER, COURSE_STATUS, COURSE_TYPE } from '@shared/constant/system.constant';

export interface Teacher {
    code?: string;
    name?: string;
    gender?: GENDER;
    phone?: string;
    introduction?: string;
    courseSalary?: CourseSalary[];
}

export interface CourseSalary {
    id?: string;
    grade?: number;
    courseSubject?: number;
    courseType?: COURSE_TYPE;
    salary?: number;
}