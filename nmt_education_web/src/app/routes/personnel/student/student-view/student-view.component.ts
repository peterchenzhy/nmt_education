import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { STColumn, STComponent } from '@delon/abc';
import { Student } from 'src/app/model/student.model';
import { COURSE_STATUS_LIST, ORDER_STATUS_LIST, RELATIONSHIP_LIST, GRADE_LIST, CAMPUS_LIST } from '@shared/constant/system.constant';

@Component({
    selector: 'app-student-view',
    templateUrl: './student-view.component.html',
})
export class StudentViewComponent implements OnInit {
    pageHeader: string;
    student: Student = {};
    @ViewChild('st', { static: true })
    st: STComponent;
    constructor(
        private fb: FormBuilder,
        private routerinfo: ActivatedRoute,
        public msgSrv: NzMessageService,
        public http: _HttpClient
    ) {
    }

    editIndex = -1;
    editObj = {};
    form: FormGroup;
    courseStatus = COURSE_STATUS_LIST;
    orderStatus: any[] = ORDER_STATUS_LIST;
    relationships: any[] = RELATIONSHIP_LIST;
    campusList: any[] = CAMPUS_LIST;
    gradeList: any[] = GRADE_LIST;
    classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];
    coursesColumns: STColumn[] = [
        { title: '订单编号', index: 'orderNo', type: 'link' },
        { title: '名称', index: 'courseName' },
        { title: '校区', index: 'campus' },
        {
            title: '课程状态',
            index: 'courseStatus',
            render: 'courseStatus'
        },
        {
            title: '上课状态',
            index: 'orderStatus',
            render: 'orderStatus'
        },
        {
            title: '操作',
            buttons: [
                {
                    text: '冻结',
                    //click: (item: any) => this.router.navigate([`/course/view/${item.courseNo}`]),
                },
                {
                    text: '恢复',
                    //click: (item: any) => this.msg.success(`报名${item.courseName}`),
                },
            ],
        },
    ];
    courses = [{ orderNo: 'order111', courseName: 'course111', campus: "二工大", courseStatus: 1, courseStatusText: "开课中", courseStatusType: "processing", orderStatus: "0", orderStatusText: "续费", orderStatusType: "purple" },
    { orderNo: 'order222', courseName: 'course222', campus: "二工大", courseStatus: 1, courseStatusText: "开课中", courseStatusType: "processing", orderStatus: "2", orderStatusText: "冻结", orderStatusType: "error" },
    { orderNo: 'order333', courseName: 'course333', campus: "二工大", courseStatus: 2, courseStatusText: "已结课", courseStatusType: "success", orderStatus: "1", orderStatusText: "完成", orderStatusType: "success" }];

    ngOnInit() {
        this.student.code = this.routerinfo.snapshot.params['code'];
        this.http.get(`/student/${this.student.code}`).subscribe(res => { this.student = res; this.form.patchValue(res); });
        this.pageHeader = `学生信息编辑 [${this.student.code}]`;
        this.form = this.fb.group({
            studentName: [null, [Validators.required]],
            gender: [null, []],
            phone: [null, []],
            parents: this.fb.array([])
        });
        const parentsList = [
            {
                key: '1',
                relation: '父亲',
                name: 'Father',
                phone: '130********'
            },
            {
                key: '2',
                relation: '母亲',
                name: 'Mother',
                phone: '131********'
            }];
        parentsList.forEach(i => {
            const field = this.createParent();
            field.patchValue(i);
            this.parents.push(field);
        });
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
    get studentName() {
        return this.form.controls.studentName;
    }
    get gender() {
        return this.form.controls.gender;
    }
    get phone() {
        return this.form.controls.phone;
    }

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
    }

}
