import { Status } from 'src/app/model/system.model';

export const SEASON_LIST = [{ label: '春季', value: 1 }, { label: '暑假', value: 2 }, { label: '秋季', value: 3 }, { label: '寒假', value: 4 }];
export enum SEASON { SPRING = 1, SUMMER, AUTUMN, WINTER }
export function getSeasonLabel(season: SEASON) {
    let obj = SEASON_LIST.find(i => { return i.value == season });
    return obj ? obj.label : "";
}

export const COURSE_STATUS_LIST: Status[] = [
    { index: 0, text: '未开始', value: 0, type: 'default', checked: false },
    { index: 1, text: '开课中', value: 1, type: 'processing', checked: false },
    { index: 2, text: '已结课', value: 2, type: 'success', checked: false },
    { index: 3, text: '已取消', value: 3, type: 'error', checked: false }];
export enum COURSE_STATUS { PENDING = 0, PROCESSING = 1, COMPLETE = 2, CALCEL = 3 }
export function getCourseStatus(status: COURSE_STATUS) {
    return COURSE_STATUS_LIST.find(i => { return i.value == status });
}

export const COURSE_SUBJECT_LIST = [
    { label: '语文', value: 1 }, { label: '数学', value: 2 }, { label: '英语', value: 3 },
    { label: '物理', value: 4 }, { label: '化学', value: 5 }, { label: '生物', value: 6 }];
export enum COURSE_SUBJECT { CHINESE = 1, MATHS, ENGLISH, PHYSICS, CHEMISTRY, BIOLOGY }
export function getCourseSubjectLabel(subject: COURSE_SUBJECT) {
    let obj = COURSE_SUBJECT_LIST.find(i => { return i.value == subject });
    return obj ? obj.label : "";
}

export const COURSE_TYPE_LIST = [{ label: '普通班', value: 1 }, { label: '自招班', value: 2 }, { label: '冲刺班', value: 3 }];
export enum COURSE_TYPE { ORDINARY = 1, INVITE, SPRINT }
export function getCourseTypeLabel(type: COURSE_TYPE) {
    let obj = COURSE_TYPE_LIST.find(i => { return i.value == type });
    return obj ? obj.label : "";
}

export const COURSE_CLASSIFICATION_LIST = [
    { label: '小学', value: 1 }, { label: '初中', value: 2 },
    { label: '高中', value: 3 }, { label: '成人', value: 4 }];
export enum COURSE_CLASSIFICATION { PRIMARY_SCHOOL = 1, MIDDLE_SCHOOL, HIGH_SCHOOL, ADULT }
export function getCourseClassificationLabel(classification: COURSE_CLASSIFICATION) {
    let obj = COURSE_CLASSIFICATION_LIST.find(i => { return i.value == classification });
    return obj ? obj.label : "";
}

export const GRADE_LIST = [
    { value: 1, label: '一年级' }, { value: 2, label: '二年级' }, { value: 3, label: '三年级' },
    { value: 4, label: '四年级' }, { value: 5, label: '五年级' }, { value: 6, label: '六年级' },
    { value: 7, label: '七年级' }, { value: 8, label: '八年级' }, { value: 9, label: '九年级' },
    { value: 11, label: '高一' }, { value: 12, label: '高二' }, { value: 13, label: '高三' }];
export enum GRADE { ONE = 1, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SENIOR_ONE = 11, SENIOR_TWO = 12, SENIOR_THREE = 13 }
export function getGradeLabel(grade: GRADE) {
    let obj = GRADE_LIST.find(i => { return i.value == grade });
    return obj ? obj.label : "";
}

export const GENDER_LIST = [{ value: 0, label: '女' }, { value: 1, label: '男' }];
export enum GENDER { FEMALE = 0, MALE = 1 }
export function getGenderLabel(gender: GENDER) {
    let obj = GENDER_LIST.find(i => { return i.value == gender });
    return obj ? obj.label : "";
}

export const ORDER_STATUS_LIST = [
    { index: 0, text: '新报', value: 1, type: 'blue', checked: false },
    { index: 1, text: '续费', value: 2, type: 'purple', checked: false },
    { index: 2, text: '完成', value: 3, type: 'success', checked: false },
    { index: 3, text: '退费', value: 4, type: 'warning', checked: false },
    { index: 4, text: '冻结', value: 5, type: 'error', checked: false },
    { index: 5, text: '无效', value: 6, type: 'default', checked: false }];
export enum ORDER_STATUS { NEW = 1, RENEW, COMPLETE, REFUND, FROZEN, INVALID }
export function getOrderStatus(status: ORDER_STATUS) {
    return ORDER_STATUS_LIST.find(i => { return i.value == status });
}

export const FEE_TYPE_LIST = [
    { value: 1, label: '普通学费' }, { value: 2, label: '试听费用' }, { value: 3, label: '材料费' },
    { value: 4, label: '试验费' }, { value: 5, label: '退费' }];
export enum FEE_TYPE { TUITION = 1, AUDITION, MATERIAL, TRAIL, REFUND }
export function getFeeTypeLabel(type: FEE_TYPE) {
    let obj = FEE_TYPE_LIST.find(i => { return i.value == type });
    return obj ? obj.label : "";
}

export const PAY_STATUS_LIST = [
    { value: 1, label: '非缴费' }, { value: 2, label: '已缴费' },
    { value: 3, label: '已冻结' }, { value: 4, label: '已退费' }];
export enum PAY_STATUS { UNPAY = 1, PAIED, FROZEN, REFUNDED }
export function getPayStatusLabel(type: PAY_STATUS) {
    let obj = PAY_STATUS_LIST.find(i => { return i.value == type });
    return obj ? obj.label : "";
}

export const PAY_METHOD_LIST = [
    { value: 1, label: '现金' }, { value: 2, label: '刷卡' },
    { value: 3, label: '微信' }, { value: 4, label: '支付宝' }];
export enum PAY_METHOD { CASH = 1, CARD, WECHART, ALIPAY }
export function getPayMethodLabel(method: PAY_METHOD) {
    let obj = PAY_METHOD_LIST.find(i => { return i.value == method });
    return obj ? obj.label : "";
}

export const RELATIONSHIP_LIST = [{ value: 1, label: '父亲' }, { value: 2, label: '母亲' }, { value: 3, label: '其他' }];
export enum RELATIONSHIP { FATHER = 1, MOTHER, OTHER }
export function getRelationshipLabel(releation: RELATIONSHIP) {
    let obj = RELATIONSHIP_LIST.find(i => { return i.value == releation });
    return obj ? obj.label : "";
}

export const CAMPUS_LIST = [{ value: 1, label: '南码头' }, { value: 2, label: '四平路' }];
export enum CAMPUS { SOUTH_QUAY = 1, SIPING_ROAD }
export function getCampusLabel(campus: CAMPUS) {
    let obj = CAMPUS_LIST.find(i => { return i.value == campus });
    return obj ? obj.label : "";
}

export const SysConstantUtil = {
    getSeasonLabel: getSeasonLabel,
    getRelationshipLabel: getRelationshipLabel,
    getPayMethodLabel: getPayMethodLabel,
    getPayStatusLabel: getPayStatusLabel,
    getFeeTypeLabel: getFeeTypeLabel,
    getOrderStatus: getOrderStatus,
    getGenderLabel: getGenderLabel,
    getGradeLabel: getGradeLabel,
    getCourseClassificationLabel: getCourseClassificationLabel,
    getCourseTypeLabel: getCourseTypeLabel,
    getCourseSubjectLabel: getCourseSubjectLabel,
    getCourseStatus: getCourseStatus,
    getCampusLabel: getCampusLabel
}