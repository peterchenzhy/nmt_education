import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { STColumn, STComponent } from '@delon/abc';
import { GRADE_LIST, CAMPUS_LIST, COURSE_SUBJECT_LIST, COURSE_TYPE_LIST, getGradeLabel, getCourseTypeLabel } from '@shared/constant/system.constant';
import { Teacher } from 'src/app/model/teacher.model';

@Component({
    selector: 'app-teacher-view',
    templateUrl: './teacher-view.component.html',
})
export class TeacherViewComponent implements OnInit {
    getGradeLabel = getGradeLabel;
    getCourseTypeLabel = getCourseTypeLabel;
    pageHeader: string;
    teacher: Teacher = {};
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
    courseTypeList = COURSE_TYPE_LIST;
    courseSubjectList: any[] = COURSE_SUBJECT_LIST;
    campusList: any[] = CAMPUS_LIST;
    gradeList: any[] = GRADE_LIST;
    classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];
    coursesColumns: STColumn[] = [
        { title: '名称', index: 'courseName' },
        { title: '校区', index: 'campus' },
        { title: '教室', index: 'classroom' },
        { title: '上课时间', index: 'startTime' },
        { title: '课程时长', index: 'duration' }
    ];
    courses = [{ orderNo: 'order111', courseName: 'course111', campus: "二工大", classroom: "101", startTime: "2020-04-05 12:00:00", duration: "90", orderStatus: "0", orderStatusText: "续费", orderStatusType: "purple" },
    { orderNo: 'order222', courseName: 'course222', campus: "二工大", classroom: "202", startTime: "2020-04-09 12:00:00", duration: "90", orderStatus: "2", orderStatusText: "冻结", orderStatusType: "error" },
    { orderNo: 'order333', courseName: 'course333', campus: "二工大", classroom: "303", startTime: "2020-04-15 14:00:00", duration: "90", orderStatus: "1", orderStatusText: "完成", orderStatusType: "success" }];

    ngOnInit() {
        this.teacher.code = this.routerinfo.snapshot.params['code'];
        this.http.get(`/student/${this.teacher.code}`).subscribe(res => { this.teacher = res; this.form.patchValue(res); });
        this.pageHeader = `教师信息编辑 [${this.teacher.code}]`;
        this.form = this.fb.group({
            name: [null, [Validators.required]],
            gender: [null, []],
            subject: [null, [Validators.required]],
            contactNo: [null, []],
            salayConfig: this.fb.array([])
        });
        const salaryList = [
            {
                id: '1',
                grade: 4,
                courseType: 1,
                salary: '500'
            },
            {
                id: '2',
                grade: 4,
                courseType: 3,
                salary: '600'
            }];
        salaryList.forEach(i => {
            const field = this.createSalary();
            field.patchValue(i);
            this.salayConfig.push(field);
        });
    }

    createSalary(): FormGroup {
        return this.fb.group({
            id: [null],
            grade: [null, [Validators.required]],
            courseType: [null, [Validators.required]],
            salary: [null, [Validators.required]]
        });
    }

    //#region get form fields
    get name() {
        return this.form.controls.name;
    }
    get gender() {
        return this.form.controls.gender;
    }
    get contactNo() {
        return this.form.controls.contactNo;
    }

    get salayConfig() {
        return this.form.controls.salayConfig as FormArray;
    }
    //#endregion

    add() {
        this.salayConfig.push(this.createSalary());
        this.edit(this.salayConfig.length - 1);
    }

    del(index: number) {
        this.salayConfig.removeAt(index);
    }

    edit(index: number) {
        if (this.editIndex !== -1 && this.editObj) {
            this.salayConfig.at(this.editIndex).patchValue(this.editObj);
        }
        this.editObj = { ...this.salayConfig.at(index).value };
        this.editIndex = index;
    }

    save(index: number) {
        this.salayConfig.at(index).markAsDirty();
        if (this.salayConfig.at(index).invalid) return;
        this.editIndex = -1;
    }

    cancel(index: number) {
        if (!this.salayConfig.at(index).value.id) {
            this.del(index);
        } else {
            this.salayConfig.at(index).patchValue(this.editObj);
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
