import { GENDER, COURSE_STATUS, COURSE_TYPE, EDIT_FLAG } from '@shared/constant/system.constant';

export interface Teacher {
    id?: number;
    code?: string;
    name?: string;
    sex?: GENDER;
    phone?: string;
    school?: string;
    birthday?: Date;
    remark?: string;
    salaryConfigList?: CourseSalary[];
    editFlag?: EDIT_FLAG;
    status?: number;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}

export interface CourseSalary {
    id?: number;
    grade?: number;
    courseSubject?: number;
    courseType?: COURSE_TYPE;
    teacherId?: number;
    unitPrice?: number;
    remark?: string;
    editFlag?: EDIT_FLAG;
    status?: number;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}