import { Status } from 'src/app/model/system.model';

export const COURSE_STATUS_LIST: Status[] = [
    { index: 0, text: '未开始', value: 0, type: 'default', checked: false },
    { index: 1, text: '开课中', value: 1, type: 'processing', checked: false },
    { index: 2, text: '已结课', value: 2, type: 'success', checked: false }];
export enum COURSE_STATUS { PENDING = 0, PROCESSING = 1, COMPLETE = 2 }

export const COURSE_SUBJECT_LIST = [
    { label: '语文', value: 0 },
    { label: '数学', value: 1 },
    { label: '英语', value: 2 }];
export enum COURSE_SUBJECT { CHINESE = 0, MATHS = 1, ENGLISH = 2 }

export const GRADE_LIST = [{ value: '1', label: '一年级' }, { value: '2', label: '二年级' }, { value: '3', label: '三年级' }];
export enum GRADE { ONE = 1, TWO = 2, THREE = 3 }

export enum GENDER { FEMALE = 0, MALE = 1 }

export const ORDER_STATUS_LIST = [
    { index: 0, text: '正常', value: 0, type: 'processing', checked: false },
    { index: 1, text: '完成', value: 1, type: 'success', checked: false, },
    { index: 2, text: '冻结', value: 2, type: 'error', checked: false, }];
export enum ORDER_STATUS { NORMAL = 0, COMPLETE = 1, FROZEN = 2 }