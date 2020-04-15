import { Course } from './course.model';
import { Student } from './student.model';
import { SysEnum } from './system.model';
import { PAY_STATUS, ORDER_STATUS } from '@shared/constant/system.constant';

export interface Order {
    id?: string;
    status?: ORDER_STATUS;
    statusDetail?: SysEnum;
    signInDate?: Date;
    course?: Course;
    student?: Student;
    payList?: Payment[];
    sessionList?: OrderSession[];
    totalPrice?: number;
    balance?: number;
}

export interface Payment {
    id?: string;
    type?: number;
    method?: number;
    status?: PAY_STATUS;
    price?: number;
    discount?: number;
    receivable?: number;
    deduction?: number;
    paied?: number;
    comment?: string;
}

export interface OrderSession {
    id?: string;
    startDateTime?: Date;
    duration?: number;
    teacher?: string;
    price?: number
}