import { Component, OnInit, ViewChild, Input, ChangeDetectorRef } from '@angular/core';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { Location } from '@angular/common';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute, Router } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { STColumn, STComponent, STChange } from '@delon/abc';
import { Student } from 'src/app/model/student.model';
import { GlobalService } from '@shared/service/global.service';
import { StudentService } from '@shared/service/student.service';
import { EDIT_FLAG } from '@shared/constant/system.constant';
import { tap } from 'rxjs/operators';
import { AppContextService } from '@shared/service/appcontext.service';
import { ResponseData, RegisterQueryParam } from 'src/app/model/system.model';

@Component({
    selector: 'app-student-view',
    templateUrl: './student-view.component.html',
})
export class StudentViewComponent implements OnInit {
    pageHeader: string = "学生信息编辑";
    student: Student = { editFlag: EDIT_FLAG.NEW };
    editIndex = -1;
    editObj = {};
    form: FormGroup;
    loading: boolean = false;
    @ViewChild('st', { static: true })
    st: STComponent;
    constructor(
        private activaterRouter: ActivatedRoute,
        public appCtx: AppContextService,
        private fb: FormBuilder,
        public msgSrv: NzMessageService,
        private modalSrv: NzModalService,
        private router: Router,
        private _location: Location,
        private cdr: ChangeDetectorRef
    ) {
    }
    courseStatus = this.appCtx.globalService.COURSE_STATUS_LIST;
    orderStatus: any[] = this.appCtx.globalService.ORDER_STATUS_LIST;
    relationships: any[] = this.appCtx.globalService.RELATIONSHIP_LIST;
    campusList: any[] = this.appCtx.globalService.CAMPUS_LIST;
    gradeList: any[] = this.appCtx.globalService.GRADE_LIST;
    classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];
    coursesColumns: STColumn[] = [
        { title: '订单编号', index: 'registrationNumber' },
        { title: '姓名', index: 'studentName' },
        { title: '课程名', index: 'courseName' },
        {
            title: '报名时间',
            index: 'registerTime',
            type: 'date',
            sort: {
                compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
            },
        },
        {
            title: '状态',
            index: 'registrationStatus',
            render: 'registrationStatus'
        },
        { title: '总金额', index: 'totalAmount' },
        { title: '余额', index: 'balanceAmount' },
        {
            title: '操作',
            buttons: [
                {
                    text: '编辑',
                    click: (item: any) => this.router.navigate([`/order/view/${item.id}`])
                },
                {
                    text: '退费',
                    click: (item: any) => this.router.navigate([`/order/refund/${item.id}`])
                }
            ],
        },
    ];

    ngOnInit() {
        this.form = this.fb.group({
            id: [null, []],
            name: [null, [Validators.required]],
            sex: [null, [Validators.required]],
            grade: [null, [Validators.required]],
            school: [null, [Validators.required]],
            birthday: [null, []],
            campus: [null, [Validators.required]],
            remark: [null, []],
            phone: [null, [Validators.required]],
            editFlag: [false, []],
            parents: this.fb.array([])
        });
        let studentStr = this.activaterRouter.snapshot.params.student;
        if (studentStr) {
            this.student = JSON.parse(studentStr);
            this.student.editFlag = EDIT_FLAG.UPDATE;
            this.pageHeader = `学生信息编辑 -- [${this.student.name}] -- [${this.student.code}] `;
        }
        this.form.patchValue(this.student);

        // const parentsList = [
        //     {
        //         key: '1',
        //         relation: '父亲',
        //         name: 'Father',
        //         phone: '130********'
        //     },
        //     {
        //         key: '2',
        //         relation: '母亲',
        //         name: 'Mother',
        //         phone: '131********'
        //     }];
        // parentsList.forEach(i => {
        //     const field = this.createParent();
        //     field.patchValue(i);
        //     this.parents.push(field);
        // });
    }

    createParent(): FormGroup {
        return this.fb.group({
            key: [null],
            relation: [null, [Validators.required]],
            name: [null, [Validators.required]],
            phone: [null, [Validators.required]]
        });
    }

    //#region get form fields
    // get studentName() {
    //     return this.form.controls.name;
    // }
    // get gender() {
    //     return this.form.controls.sex;
    // }
    // get phone() {
    //     return this.form.controls.phone;
    // }

    get parents() {
        return this.form.controls.parents as FormArray;
    }
    //#endregion

    add() {
        this.parents.push(this.createParent());
        this.edit(this.parents.length - 1);
    }

    del(index: number) {
        this.parents.removeAt(index);
    }

    edit(index: number) {
        if (this.editIndex !== -1 && this.editObj) {
            this.parents.at(this.editIndex).patchValue(this.editObj);
        }
        this.editObj = { ...this.parents.at(index).value };
        this.editIndex = index;
    }

    save(index: number) {
        this.parents.at(index).markAsDirty();
        if (this.parents.at(index).invalid) return;
        this.editIndex = -1;
    }

    cancel(index: number) {
        if (!this.parents.at(index).value.key) {
            this.del(index);
        } else {
            this.parents.at(index).patchValue(this.editObj);
        }
        this.editIndex = -1;
    }

    _submitForm() {
        Object.keys(this.form.controls).forEach(key => {
            this.form.controls[key].markAsDirty();
            this.form.controls[key].updateValueAndValidity();
        });
        if (this.form.invalid) return;
        this.loading = true;
        this.appCtx.studentService.saveStudent(this.form.value)
            .pipe(
                tap(() => { this.loading = false; }, () => { this.loading = false; })
            )
            .subscribe((res) => {
                this.modalSrv.success({
                    nzTitle: '处理结果',
                    nzContent: '学生信息保存成功！',
                    nzOnOk: () => {
                        this.router.navigate(["/personnel/student/list"]);
                    }
                });
            });
    }

    goBack() {
        this._location.back();
    }

    courseLoaded = false;
    pager = {
        front: false
    };
    courseQueryParam: RegisterQueryParam = { pageNo: 1, pageSize: 10 };
    courses: ResponseData = { list: [], total: 0 };
    onSelectedTabChanged(event: any) {
        if (!this.courseLoaded && event.index == 1) {
            this.getRegisteredCourses();
            return;
        }
        if (!this.accountLoaded && event.index == 2) {
            this.getAccountDetails();
            return;
        }
    }

    getRegisteredCourses() {
        if (!this.student.id) {
            return;
        }
        this.loading = true;
        this.courseQueryParam.studentId = this.student.id;
        this.appCtx.courseService.registerSearch(this.courseQueryParam)
            .pipe(
                tap(() => { this.loading = false; }, () => { this.loading = false; })
            )
            .subscribe((res: ResponseData) => {
                res.list = res.list || [];
                this.courseLoaded = true;
                this.courses = res;
                this.cdr.detectChanges();
            });
    }

    courseStChange(e: STChange) {
        switch (e.type) {
            case 'checkbox':
                break;
            case 'pi':
                this.courseQueryParam.pageNo = e.pi;
                this.getRegisteredCourses();
                break;
        }
    }


    accountColumns: STColumn[] = [
        {
            title: '时间',
            index: 'createTime',
            type: 'date',
            sort: {
                compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
            },
        },
        {
            title: '类型',
            index: 'type'
        },
        { title: '变化前余额', index: 'beforeAmount' },
        { title: '变化后余额', index: 'amount' },
        { title: '备注', index: 'remark' }
    ];
    accountLoaded = false;
    accountQueryParam: RegisterQueryParam = { pageNo: 1, pageSize: 10 };
    account: ResponseData = { list: [], total: 0 };
    getAccountDetails() {
        if (!this.student.id) {
            return;
        }
        this.loading = true;
        //this.accountQueryParam.studentId = this.student.id;
        this.appCtx.studentService.getAccountDetails(this.student.id)
            .pipe(
                tap(() => { this.loading = false; }, () => { this.loading = false; })
            )
            .subscribe((res: ResponseData) => {
                res.list = res.list || [];
                this.accountLoaded = true;
                this.account = res;
                this.cdr.detectChanges();
            });
    }

    accountStChange(e: STChange) {
        switch (e.type) {
            case 'checkbox':
                break;
            case 'pi':
                this.accountQueryParam.pageNo = e.pi;
                this.getAccountDetails();
                break;
        }
    }
}
