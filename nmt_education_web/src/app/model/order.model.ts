import { Course } from './course.model';
import { Student } from './student.model';
import { SysEnum } from './system.model';
import { PAY_STATUS, ORDER_STATUS, EDIT_FLAG, ORDER_TYPE } from '@shared/constant/system.constant';

export interface Order {
    id?: number;
    registrationStatus?: ORDER_STATUS;
    statusDetail?: SysEnum;
    registrationType?: ORDER_TYPE;
    times?: number;
    courseId?: number;
    course?: Course;
    studentId?: number;
    student?: Student;
    registerExpenseDetail?: Payment[];
    courseScheduleList?: OrderSession[];
    courseScheduleIds?: number[];
    feeStatus?: PAY_STATUS;
    totalPrice?: number;
    balance?: number;
    campus?: number;
    remark?: string;
    editFlag?: EDIT_FLAG;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}

export interface Payment {
    id?: string;
    feeType?: number;
    payment?: number;
    feeStatus?: PAY_STATUS;
    perAmount?: number;
    count?:number;
    discount?: number;
    receivable?: number;
    deduction?: number;
    amount?: number;
    remark?: string;
}

export interface OrderSession {
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