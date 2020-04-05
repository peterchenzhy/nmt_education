import { GENDER, RELATIONSHIP } from '@shared/constant/system.constant';

export interface Student {
    code?: string;
    name?: string;
    gender?: GENDER;
    phone?: string;
    school?: string;
    birthday?: Date;
    remark?: string;
    campus?: number;
    parents?: Parent[];
}

export interface Parent {
    code?: string;
    name?: string;
    relation?: RELATIONSHIP;
    phone?: string;
}