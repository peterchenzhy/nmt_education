import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { Order, Payment } from 'src/app/model/order.model';
import { SysConstantUtil, FEE_TYPE_LIST, PAY_STATUS_LIST, PAY_METHOD_LIST } from '@shared/constant/system.constant';
import { Course } from 'src/app/model/course.model';

@Component({
    selector: 'app-order-view',
    templateUrl: './order-view.component.html',
    styles: [`
    .enroll-selector{width:200px;}
    ::ng-deep .enroll-selector .ant-select-selection--single{height:22px;}
    ::ng-deep .enroll-selector .ant-select-selection__rendered{line-height:20px;}`]
})
export class OrderViewComponent implements OnInit {
    feeTypeList = FEE_TYPE_LIST;
    payStatusList = PAY_STATUS_LIST;
    payMethodList = PAY_METHOD_LIST;
    sysConstUtil = SysConstantUtil;
    form: FormGroup;
    editFeeIndex = -1;
    order: Order = {
        course: {},
        payList: []
    };

    constructor(
        private fb: FormBuilder,
        private activaterRouter: ActivatedRoute,
        public msgSrv: NzMessageService,
        public http: _HttpClient
    ) {
    }
    ngOnInit(): void {
        let studentStr = this.activaterRouter.snapshot.params.student;
        if (studentStr) {
            this.order.student = JSON.parse(studentStr);
        }
        let courseStr = this.activaterRouter.snapshot.params.course;
        if (courseStr) {
            this.order.course = JSON.parse(courseStr);
        }
        this.form = this.fb.group({
            studentCode: [null, [Validators.required]],
            courseCode: [null, [Validators.required]],
            payList: this.fb.array([])
        });
    }
    createPay(): FormGroup {
        return this.fb.group({
            id: [null],
            type: [null, [Validators.required]],
            method: [null, [Validators.required]],
            status: [1, [Validators.required]],
            price: [null, [Validators.required]],
            discount: [1, [Validators.required]],
            receivable: [null, [Validators.required]],
            deduction: [0, [Validators.required]],
            paied: [0, [Validators.required]],
            comment: [null, [Validators.required]]
        });
    }
    get payList() {
        return this.form.controls.payList as FormArray;
    }

    listOfOption: Array<Course> = [];
    nzFilterOption = () => true;
    search(value: string): void {
        this.http
            .get("/course")
            .subscribe(data => {
                const listOfOption: Array<Course> = [];
                data.forEach(item => {
                    listOfOption.push(item);
                });
                this.listOfOption = listOfOption;
            });
    }

    courseSelected(value: string) {
        this.order.course = this.listOfOption.find(c => { return c.code == value; });
        const payList: Payment[] = [
            {
                id: "1", type: 1, method: 4, status: 1,
                price: 1000, discount: 1, receivable: 400, paied: 400, comment: ""
            },
            {
                id: "2", type: 3, method: 4, status: 1, price: 1000,
                discount: 0.9, receivable: 900, deduction: 200, paied: 700, comment: ""
            }];
        payList.forEach(i => {
            const field = this.createPay();
            field.patchValue(i);
            this.payList.push(field);
        });
    }

    clearSelectedCourse(){
        this.order.course={};
        this.payList.clear();
    }
}