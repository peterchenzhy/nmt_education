import { Parent } from './parent.model';

export class Student {
    id?: string;
    name?: string;
    gender?: number;
    contactNo?: string;
    balance?: number;
    parents?: Parent[];
}