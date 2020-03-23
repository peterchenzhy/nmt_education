import { GENDER, RELATIONSHIP } from '@shared/constant/system.constant';

export interface Student {
    code?: string;
    name?: string;
    gender?: GENDER;
    contactNo?: string;
    balance?: number;
    parents?: Parent[];
}

export interface Parent {
    code?: string;
    name?: string;
    relation?: RELATIONSHIP;
    contactNo?: string;
}