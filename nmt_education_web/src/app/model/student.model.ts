import { GENDER, RELATIONSHIP, EDIT_FLAG } from '@shared/constant/system.constant';

export interface Student {
    id?: string;
    studentCode?: string;
    name?: string;
    sex?: GENDER;
    phone?: string;
    grade?: number;
    school?: string;
    birthday?: Date;
    remark?: string;
    campus?: number;
    contractList?: Parent[];
    status?: number;
    createTime?: Date;
    creator?: number;
    operateTime?: Date;
    operator?: number;
    editFlag?: EDIT_FLAG; 
}

export interface Parent {
    code?: string;
    name?: string;
    relation?: RELATIONSHIP;
    phone?: string;
}