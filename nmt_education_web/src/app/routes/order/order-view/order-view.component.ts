import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectorRef } from '@angular/core';
import { Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { Order, Payment } from 'src/app/model/order.model';
import { Course } from 'src/app/model/course.model';
import { AppContextService } from '@shared/service/appcontext.service';
import { ORDER_STATUS, EDIT_FLAG, PAY_STATUS, ORDER_TYPE } from '@shared/constant/system.constant';
import { STData, STComponent, STColumn, STChange } from '@delon/abc';

@Component({
    selector: 'app-order-view',
    templateUrl: './order-view.component.html',
    styles: [`
    .enroll-selector{width:200px;}
    ::ng-deep .enroll-selector .ant-select-selection--single{height:22px;}
    ::ng-deep .enroll-selector .ant-select-selection__rendered{line-height:20px;}`]
})
export class OrderViewComponent implements OnInit {
    feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
    payStatusList = this.appCtx.globalService.PAY_STATUS_LIST;
    payMethodList = this.appCtx.globalService.PAY_METHOD_LIST;
    registrationTypeList = this.appCtx.globalService.ORDER_TYPE_LIST;
    registrationStatusList = this.appCtx.globalService.ORDER_STATUS_LIST;
    form: FormGroup;
    editFeeIndex = -1;
    order: Order = {
        student: {},
        course: {},
        courseScheduleIds: [],
        registerExpenseDetail: [],
        editFlag: EDIT_FLAG.NEW
    };

    constructor(
        private appCtx: AppContextService,
        private modalSrv: NzModalService,
        private fb: FormBuilder,
        private activaterRouter: ActivatedRoute,
        public msgSrv: NzMessageService,
        private cdr: ChangeDetectorRef,
        private _location: Location
    ) {
    }

    @ViewChild('st', { static: true })
    st: STComponent;
    sessionColumns: STColumn[] = [
        { title: '', index: 'id', type: 'checkbox' },
        { title: '课时', index: 'courseTimes' },
        { title: '任课教师', index: 'teacherId', render: "showTeacher" },
        { title: '上课时间', index: 'courseDatetime', type: 'date', dateFormat: 'YYYY-MM-DD HH:mm' },
        { title: '时长', index: 'perTime' }
    ];
    sessionsSTData: STData[] = [];
    selectedSessions: STData[] = [];
    ngOnInit(): void {
        this.form = this.fb.group({
            id: [null, []],
            registrationStatus: [ORDER_STATUS.NORMAL, [Validators.required]],
            registrationType: [ORDER_TYPE.NEW, [Validators.required]],
            courseScheduleIds: [, []],
            feeStatus: [PAY_STATUS.PAIED, [Validators.required]],
            campus: [null, [Validators.required]],
            remark: [null, []],
            editFlag: [EDIT_FLAG.NO_CHANGE, [Validators.required]],
            studentId: [null, [Validators.required]],
            courseId: [null, [Validators.required]],
            registerExpenseDetail: this.fb.array([])
        });
        let orderId = this.activaterRouter.snapshot.params.id;
        if (orderId) {
            this.appCtx.courseService.getRegisterDetails(orderId)
                .subscribe(res => {
                    this.order = res;
                    this.order.editFlag = EDIT_FLAG.UPDATE;
                    this.form.patchValue(this.order);
                    this.order.registerExpenseDetail.forEach(i => {
                        const field = this.createPay();
                        field.patchValue(i);
                        this.registerExpenseDetail.push(field);
                    });
                });
            return;
        }
        let studentStr = this.activaterRouter.snapshot.params.student;
        if (studentStr) {
            this.order.student = JSON.parse(studentStr);
            this.order.studentId = this.order.student.id;
        }
        let courseStr = this.activaterRouter.snapshot.params.course;
        if (courseStr) {
            this.order.course = JSON.parse(courseStr);
            this.order.courseId = this.order.course.id;
        }
        this.form.patchValue(this.order);
    }
    createPay(): FormGroup {
        return this.fb.group({
            id: [null],
            feeType: [null, [Validators.required]],
            payment: [null, [Validators.required]],
            feeStatus: [PAY_STATUS.PAIED, [Validators.required]],
            perAmount: [0, [Validators.required]],
            count: [0, [Validators.required]],
            discount: [1, [Validators.required]],
            receivable: [0, []],
            deduction: [0, []],
            amount: [0, [Validators.required]],
            remark: ["", []],
            editFlag: [EDIT_FLAG.NEW, []]
        });
    }

    get isEditOrder() {
        return this.form.value.editFlag != EDIT_FLAG.NEW;
    }

    get registerExpenseDetail() {
        return this.form.controls.registerExpenseDetail as FormArray;
    }

    coursesOfOption: Array<Course> = [];
    nzFilterOption = () => true;
    search(value: string): void {
        if (!value || value == "") {
            return;
        }
        this.appCtx.courseService.fuzzyQueryCourses(value)
            .subscribe((data: Course[]) => {
                this.coursesOfOption = data;
            });
    }

    courseSelected(value: number) {
        this.appCtx.courseService.getCourseDetails(value)
            .subscribe((res: Course) => {
                this.order.courseId = res.id;
                this.order.course = res;
                if (this.order.editFlag == EDIT_FLAG.NEW) {
                    this.order.course.courseExpenseList.forEach(i => {
                        let pay: Payment = {};
                        pay.feeType = i.type;
                        pay.perAmount = i.price;
                        const field = this.createPay();
                        field.patchValue(pay);
                        this.registerExpenseDetail.push(field);
                    });
                    this.form.get("campus").setValue(this.order.course.campus);
                }
            });

    }

    clearSelectedCourse() {
        this.order.course = {};
        this.form.patchValue({ courseScheduleIds: [] });
        this.selectedSessions = [];
        this.registerExpenseDetail.clear();
    }

    onPayInfoChanged(index: number, event: any) {
        let payObject = this.registerExpenseDetail.at(index) as FormGroup;
        let newValue = this.calPayAmount(payObject.value);
        payObject.get("receivable").setValue(newValue.receivable);
        payObject.get("amount").setValue(newValue.amount);
        payObject.markAsDirty();


    }
    calPayAmount(payVal: any): any {
        let perAmount = payVal.perAmount || 0;
        let count = payVal.count || 0;
        let discount = payVal.discount || 1;
        payVal.receivable = (perAmount * count).toFixed(2);
        payVal.amount = (perAmount * count * discount).toFixed(2);

        if (payVal.editFlag == EDIT_FLAG.NO_CHANGE) {
            payVal.editFlag = EDIT_FLAG.UPDATE;
        }
        return payVal;
    }
    stChange(e: STChange) {
        switch (e.type) {
            case 'checkbox':
                this.selectedSessions = e.checkbox!;
                this.cdr.detectChanges();
                break;
        }
    }
    selectSessions(tpl: TemplateRef<{}>) {
        this.sessionsSTData = this.order.course.courseScheduleList;
        this.selectedSessions = [];
        this.sessionsSTData.forEach(s => {
            s.checked = this.form.value.courseScheduleIds.find(id => { return id == s.id; }) != null;
            if (s.checked) {
                this.selectedSessions.push(s);
            }
        });
        this.modalSrv.create({
            nzTitle: "选择报名课时",
            nzContent: tpl,
            nzWidth: 700,
            nzOnOk: () => {
                let sessionIds = this.selectedSessions.map(d => { return d.id; });
                this.form.patchValue({ courseScheduleIds: sessionIds });
                let feeIndex = this.registerExpenseDetail.value.findIndex(function (fee) { return fee.feeType == 1; });
                if (feeIndex > -1) {
                    let payObject = this.registerExpenseDetail.at(feeIndex);
                    payObject.value.count = sessionIds.length;
                    let newValue = this.calPayAmount(payObject.value);
                    payObject.patchValue(newValue, { emitEvent: true });
                    payObject.markAsDirty();
                    // this.onPayInfoChanged(feeIndex);
                }
            },
        });
    }
    goBack() {
        this._location.back();
    }
    _submitForm() {
        Object.keys(this.form.controls).forEach(key => {
            this.form.controls[key].markAsDirty();
            this.form.controls[key].updateValueAndValidity();
            if (key == "registerExpenseDetail") {
                let feeDetailCtrl: any = this.form.controls[key];
                feeDetailCtrl.controls.forEach(fee => {
                    Object.keys(fee.controls).forEach(key => {
                        fee.controls[key].markAsDirty();
                        fee.controls[key].updateValueAndValidity();
                    });
                });
            }
        });
        if (this.form.invalid) return;
        this.appCtx.courseService.registerCourse(this.form.value).subscribe((res) => {
            this.goBack();
        });
    }
}
