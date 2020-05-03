import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject } from 'rxjs';
import { HttpService } from './http.service';
import { SysEnums, SysEnum } from 'src/app/model/system.model';
import { COURSE_STATUS, COURSE_TYPE, GENDER, ORDER_STATUS, PAY_STATUS, RELATIONSHIP, ORDER_TYPE } from '@shared/constant/system.constant';

@Injectable()
export class GlobalService {
    constructor(
        private httpService: HttpService,
        private injector: Injector
    ) {
    }

    public SEASON_LIST: SysEnum[] = [];
    public getSeasonLabel(season: number) {
        let obj = this.SEASON_LIST.find(i => { return i.value == season });
        return obj ? obj.label : "";
    }

    public COURSE_STATUS_LIST: SysEnum[] = [];
    public getCourseStatus(status: COURSE_STATUS) {
        return this.COURSE_STATUS_LIST.find(i => { return i.value == status });
    }

    public COURSE_SUBJECT_LIST: SysEnum[] = [];
    public getCourseSubjectLabel(subject: number) {
        let obj = this.COURSE_SUBJECT_LIST.find(i => { return i.value == subject });
        return obj ? obj.label : "";
    }

    public COURSE_TYPE_LIST: SysEnum[] = [];
    public getCourseTypeLabel(type: COURSE_TYPE) {
        let obj = this.COURSE_TYPE_LIST.find(i => { return i.value == type });
        return obj ? obj.label : "";
    }

    public COURSE_CLASSIFICATION_LIST: SysEnum[] = [];
    public getCourseClassificationLabel(classification: number) {
        let obj = this.COURSE_CLASSIFICATION_LIST.find(i => { return i.value == classification });
        return obj ? obj.label : "";
    }

    public GRADE_LIST: SysEnum[] = [];
    public getGradeLabel(grade: number) {
        let obj = this.GRADE_LIST.find(i => { return i.value == grade });
        return obj ? obj.label : "";
    }

    public GENDER_LIST = [{ value: 0, label: '女' }, { value: 1, label: '男' }];
    public getGenderLabel(gender: GENDER) {
        let obj = this.GENDER_LIST.find(i => { return i.value == gender });
        return obj ? obj.label : "";
    }

    public ORDER_TYPE_LIST = [{ value: 1, label: '新报' }, { value: 2, label: '续报' }, { value: 3, label: '试听课' }];
    public getOrderType(type: ORDER_TYPE) {
        return this.ORDER_TYPE_LIST.find(i => { return i.value == type });
    }

    public ORDER_STATUS_LIST: SysEnum[] = [];
    public getOrderStatus(status: ORDER_STATUS) {
        return this.ORDER_STATUS_LIST.find(i => { return i.value == status });
    }

    public FEE_TYPE_LIST: SysEnum[] = [];
    public getFeeTypeLabel(type: number) {
        let obj = this.FEE_TYPE_LIST.find(i => { return i.value == type });
        return obj ? obj.label : "";
    }

    public PAY_STATUS_LIST: SysEnum[] = [];
    public getPayStatusLabel(type: PAY_STATUS) {
        let obj = this.PAY_STATUS_LIST.find(i => { return i.value == type });
        return obj ? obj.label : "";
    }

    public PAY_METHOD_LIST = [
        { value: 1, label: '现金' }, { value: 2, label: '刷卡' },
        { value: 3, label: '微信' }, { value: 4, label: '支付宝' }];
    public getPayMethodLabel(method: number) {
        let obj = this.PAY_METHOD_LIST.find(i => { return i.value == method });
        return obj ? obj.label : "";
    }

    public RELATIONSHIP_LIST = [{ value: 1, label: '父亲' }, { value: 2, label: '母亲' }, { value: 3, label: '其他' }];
    public getRelationshipLabel(releation: RELATIONSHIP) {
        let obj = this.RELATIONSHIP_LIST.find(i => { return i.value == releation });
        return obj ? obj.label : "";
    }

    public CAMPUS_LIST: SysEnum[] = [];
    public getCampusLabel(campus: number) {
        let obj = this.CAMPUS_LIST.find(i => { return i.value == campus });
        return obj ? obj.label : "";
    }

    public loadSystemEnums() {
        this.httpService.loadSystemEnums()
            .subscribe((res: SysEnums) => {
                this.SEASON_LIST = res.season;
                this.COURSE_TYPE_LIST = res.courseType;
                this.COURSE_SUBJECT_LIST = res.courseSubject;
                this.COURSE_STATUS_LIST = res.courseStatus;
                this.COURSE_CLASSIFICATION_LIST = res.courseClassification;
                this.ORDER_STATUS_LIST = res.registrationStatus;
                this.GRADE_LIST = res.grade;
                this.CAMPUS_LIST = res.campus;
                this.FEE_TYPE_LIST = res.feeType;
                this.PAY_STATUS_LIST = res.feeStatus;
            })
    }
}