import { SysEnum } from './system.model';
import { COURSE_STATUS, COURSE_TYPE, EDIT_FLAG } from '@shared/constant/system.constant';
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
    editFlag?: EDIT_FLAG;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}


export interface CourseSession {
    id?: string;
    startDateTime?: Date;
    duration?: number;
    teacher?: string;
    price?: number;
    editFlag?: EDIT_FLAG;
    status?: number;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}

export interface CourseFee {
    id?: string;
    type?: number;
    price?: number;
    editFlag?: EDIT_FLAG;
    status?: number;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
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