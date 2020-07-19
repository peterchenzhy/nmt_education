import { Course } from './course.model';
import { Student } from './student.model';
import { SysEnum } from './system.model';
import { PAY_STATUS, ORDER_STATUS, EDIT_FLAG, ORDER_TYPE, SIGNIN, FeeDirection } from '@shared/constant/system.constant';


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
    expenseDetailFlowVoList?: ExpenseDetailFlowVo[];
}
export interface ExpenseDetailFlowVo {
     id?: number;
     registrationId?: number;
     registerExpenseDetailId?: number;
     feeType?:number;
     type?: string;
     perAmount?: string;
     amount?: string;
     count?: number ;
     payment?:number;
     discount?:string;
     remark?:string;
     operateTime?:Date ;
}

export interface RegisterSummary {
    balanceAmount?: string;
    courseId?: number;
    courseName?: string
    createTime?: string;
    creator?: number;
    feeStatus?: PAY_STATUS;
    id?: number;
    operateTime?: string;
    operator?: number;
    registerTime?: string;
    registrationNumber?: string
    registrationStatus?: ORDER_STATUS;
    registrationType?: ORDER_TYPE;
    remark?: string;
    status?: number;
    studentId?: number;
    studentName?: string;
    times?: number;
    totalAmount?: string;
}

export interface Payment {
    id?: string;
    feeType?: number;
    feeDirection?:FeeDirection;
    payment?: number;
    feeStatus?: PAY_STATUS;
    perAmount?: number;
    count?: number;
    discount?: number;
    receivable?: number;
    deduction?: number;
    amount?: string;
    remark?: string;
    editFlag?: EDIT_FLAG;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
}

export interface OrderSession {
    id?: number;
    registerSummaryId?: number;
    courseId?: number;
    courseDatetime?: Date;
    perTime?: number;
    signIn?: number;
    studentSignIn: SIGNIN;
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

export interface OrderRefund {
    itemList?: Array<any>;
    payment?: number;
    registerId?: number;
    remark?: string;
}
