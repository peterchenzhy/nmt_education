import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { Location } from '@angular/common';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { STColumn, STComponent } from '@delon/abc';
import { Student } from 'src/app/model/student.model';
import { GlobalService } from '@shared/service/global.service';
import { StudentService } from '@shared/service/student.service';
import { EDIT_FLAG } from '@shared/constant/system.constant';

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
    @ViewChild('st', { static: true })
    st: STComponent;
    constructor(
        private activaterRouter: ActivatedRoute,
        private globalService: GlobalService,
        private studentService: StudentService,
        private fb: FormBuilder,
        public msgSrv: NzMessageService,
        public http: _HttpClient,
        private _location: Location
    ) {
    }
    courseStatus = this.globalService.COURSE_STATUS_LIST;
    orderStatus: any[] = this.globalService.ORDER_STATUS_LIST;
    relationships: any[] = this.globalService.RELATIONSHIP_LIST;
    campusList: any[] = this.globalService.CAMPUS_LIST;
    gradeList: any[] = this.globalService.GRADE_LIST;
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
    // courses = [{ orderNo: 'order111', courseName: 'course111', campus: "二工大", courseStatus: 1, courseStatusText: "开课中", courseStatusType: "processing", orderStatus: "0", orderStatusText: "续费", orderStatusType: "purple" },
    // { orderNo: 'order222', courseName: 'course222', campus: "二工大", courseStatus: 1, courseStatusText: "开课中", courseStatusType: "processing", orderStatus: "2", orderStatusText: "冻结", orderStatusType: "error" },
    // { orderNo: 'order333', courseName: 'course333', campus: "二工大", courseStatus: 2, courseStatusText: "已结课", courseStatusType: "success", orderStatus: "1", orderStatusText: "完成", orderStatusType: "success" }];
    courses = [];
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
            this.pageHeader = `学生信息编辑 [${this.student.code}]`;
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

        this.studentService.saveStudent(this.form.value).subscribe((res) => {
            this.goBack();
        });
    }

    goBack() {
        this._location.back();
    }
}
