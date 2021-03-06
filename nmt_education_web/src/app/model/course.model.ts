import { SysEnum } from './system.model';
import { COURSE_STATUS, COURSE_TYPE, EDIT_FLAG } from '@shared/constant/system.constant';
import { Teacher } from './teacher.model';

export interface Course {

    id?: number;
    code?: string;
    name?: string;
    description?: string;
    year?: Date | number;
    season?: number;
    startDate?: Date;
    endDate?: Date;
    perTime?: number;
    courseClassification?: number;
    grade?: number;
    courseSubject?: number;
    courseType?: COURSE_TYPE;
    courseStatus?: COURSE_STATUS;
    status?: COURSE_STATUS;
    statusDetail?: SysEnum;
    teacher?: Teacher;
    teacherId?: Teacher | number;
    totalStudent?: number;
    times?: number;
    campus?: number;
    classroom?: number;
    courseExpenseList?: CourseFee[];
    courseScheduleList?: CourseSession[];
    courseRegular?: string;
    remark?: string;
    editFlag?: EDIT_FLAG;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}


export interface CourseSession {
    id?: number;
    courseId?: number;
    courseDatetime?: Date;
    perTime?: number;
    signIn?: number;
    teacherId?: number;
    teacherPrice?: number;
    editFlag?: EDIT_FLAG;
    courseTimes?: number;
    status?: number;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}

export interface CourseFee {
    id?: number;
    courseId?: number;
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