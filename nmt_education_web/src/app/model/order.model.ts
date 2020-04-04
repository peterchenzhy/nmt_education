import { Course } from './course.model';
import { Student } from './student.model';
import { Status } from './system.model';
import { FEE_TYPE, PAY_METHOD, PAY_STATUS, ORDER_STATUS } from '@shared/constant/system.constant';

export interface Order {
    id?: string;
    status?: ORDER_STATUS;
    statusDetail?: Status;
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
    type?: FEE_TYPE;
    method?: PAY_METHOD;
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