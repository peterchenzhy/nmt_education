import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectorRef } from '@angular/core';
import { Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { Order, Payment, OrderRefund } from 'src/app/model/order.model';
import { Course } from 'src/app/model/course.model';
import { AppContextService } from '@shared/service/appcontext.service';
import { ORDER_STATUS, EDIT_FLAG, PAY_STATUS, ORDER_TYPE, SIGNIN, FEE_TYPE, FeeDirection } from '@shared/constant/system.constant';
import { STData, STComponent, STColumn, STChange } from '@delon/abc';
import { tap } from 'rxjs/operators';

@Component({
    selector: 'app-order-refund',
    templateUrl: './order-refund.component.html',
    styles: [`
    .enroll-selector{width:200px;}
    ::ng-deep .enroll-selector .ant-select-selection--single{height:22px;}
    ::ng-deep .enroll-selector .ant-select-selection__rendered{line-height:20px;}`]
})
export class OrderRefundComponent implements OnInit {
    feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
    payStatusList = this.appCtx.globalService.PAY_STATUS_LIST;
    payMethodList = this.appCtx.globalService.PAY_METHOD_LIST;
    registrationTypeList = this.appCtx.globalService.ORDER_TYPE_LIST;
    registrationStatusList = this.appCtx.globalService.ORDER_STATUS_LIST;
    form: FormGroup;
    toAccount = false;
    editFeeIndex = -1;
    order: Order = {
        registrationStatus: ORDER_STATUS.NORMAL,
        student: {},
        course: { teacher: {} },
        courseScheduleList: [],
        registerExpenseDetail: [],
        expenseDetailFlowVoList: [],
        editFlag: EDIT_FLAG.NEW
    };
    orderRefund: OrderRefund = {
        itemList: []
    };
    loading: boolean = true;

    constructor(
        public appCtx: AppContextService,
        private modalSrv: NzModalService,
        private fb: FormBuilder,
        private activaterRouter: ActivatedRoute,
        public msgSrv: NzMessageService,
        private cdr: ChangeDetectorRef,
        private _location: Location,
        private router: Router
    ) {
    }

    @ViewChild('st', { static: true })
    st: STComponent;
    orderFeeColumns: STColumn[] = [
        { title: '', index: 'id', type: 'checkbox' },
        { title: '费用类型', index: 'feeType', render: "feeType" },
        { title: '支付方式', index: 'payment', render: "payment" },
        { title: '费用', index: 'amount' },
        { title: '支付状态', index: 'feeStatus', render: "feeStatus" },
        { title: '课时时间', index: 'courseDatetime', type: 'date', dateFormat: 'YYYY-MM-DD HH:mm' },
        { title: '备注', index: 'remark' }
    ];
    orderFeeSTData: STData[] = [];
    selectedFee: STData[] = [];
    ngOnInit(): void {
        this.form = this.fb.group({
            registerId: [0, []],
            payment: [null, [Validators.required]],
            remark: ["", []],
            itemList: this.fb.array([]),
        });
        let orderId = this.activaterRouter.snapshot.params.id;
        this.orderRefund.registerId = orderId;
        this.form.patchValue(this.orderRefund);
        if (orderId) {
            this.appCtx.courseService.getRegisterDetails(orderId)
                .pipe(
                    tap(() => { this.loading = false; }, () => { this.loading = false; })
                ).subscribe(res => {
                    this.order = res;
                    this.order.course.teacher = {};
                    this.order.editFlag = EDIT_FLAG.UPDATE;
                    this.orderFeeSTData = this.order.courseScheduleList;
                    let payFeeList = this.order.registerExpenseDetail.filter(f => f.feeDirection == FeeDirection.PAY);
                    let sessionFee = payFeeList.filter(f => f.feeType == FEE_TYPE.SESSION);
                    this.orderFeeSTData.forEach(s => {
                        s.disabled = s.studentSignIn == SIGNIN.SIGNIN || s.studentSignIn == SIGNIN.REFUND;
                        if (sessionFee.length > 0) {
                            s.amount = (sessionFee[0].perAmount * sessionFee[0].discount).toFixed(2);
                            s.feeType = sessionFee[0].feeType;
                            s.payment = sessionFee[0].payment;
                            s.feeStatus = sessionFee[0].feeStatus;;
                            s.remark = sessionFee[0].remark;
                        }
                    });
                    let otherFee = payFeeList.filter(f => f.feeType != FEE_TYPE.SESSION);
                    otherFee.forEach(f => {
                        this.orderFeeSTData.push({
                            disabled: f.feeStatus == PAY_STATUS.REFUNDED,
                            feeStatus: f.feeStatus,
                            amount: f.amount,
                            feeType: f.feeType,
                            payment: f.payment,
                            remark: f.remark
                        });
                    });
                });
        }
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
                    });
                    this.form.get("campus").setValue(this.order.course.campus);
                }
            });

    }


    stChange(e: STChange) {
        switch (e.type) {
            case 'checkbox':
                this.selectedFee = e.checkbox!;
                this.cdr.detectChanges();
                break;
        }
    }

    confirmRefundFee() {
        Object.keys(this.form.controls).forEach(key => {
            this.form.controls[key].markAsDirty();
            this.form.controls[key].updateValueAndValidity();
        });
        if (this.form.invalid) return;
        let refundAmount = 0;
        this.selectedFee.forEach(f => {
            refundAmount += parseFloat(f.amount);
        });
        let refundAmountShow =  refundAmount.toFixed(2);
        let msg;
        if(this.toAccount){
          msg = `共退费[${refundAmountShow}]元，退费将直接进入结余账户` ;
        }else{
          msg = `共退费[${refundAmountShow}]元` ;
        }
        this.modalSrv.confirm({
            nzTitle: "退费确认",
            nzContent: msg,
            nzOnOk: () => {
                this._submitForm();
            }
        });
    }
    goBack() {
        this._location.back();
    }
    _submitForm() {
        let refundObj = this.form.value;
        refundObj.toAccount = this.toAccount;
        this.selectedFee.forEach(fee => {
            refundObj.itemList.push({
                amount: fee.amount,
                feeType: fee.feeType,
                registerId: refundObj.registerId,
                registerSummaryId: fee.registerSummaryId
            });
        });
        this.loading = true;
        this.appCtx.courseService.refundFee(refundObj)
            .pipe(
                tap(() => { this.loading = false; }, () => { this.loading = false; })
            ).subscribe((res) => {
                this.modalSrv.success({
                    nzTitle: '处理结果',
                    nzContent: '订单退费成功！',
                    nzOnOk: () => {
                        this.router.navigate(["/order/list"]);
                    }
                });
            });
    }

    @ViewChild('payst', { static: true })
    payst: STComponent;
    payDetailsColumns: STColumn[] = [
        { title: '费用类型', index: 'feeType', render: "feeType" },
        { title: '原价', index: 'perAmount' },
        { title: '折扣', index: 'discount' },
        { title: '数量', index: 'count' },
        { title: '总金额', index: 'amount' },
        { title: '实际支付', index: 'amountPayActually' },
        { title: '支付方式', index: 'payment', render: "payment" },
        { title: '支付状态', index: 'type' },
        { title: '支付时间', index: 'operateTime', type: 'date', dateFormat: 'YYYY-MM-DD HH:mm' },
        { title: '备注', index: 'remark' }
    ];
}
