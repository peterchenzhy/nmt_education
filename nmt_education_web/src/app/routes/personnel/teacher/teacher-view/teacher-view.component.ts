import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { Location } from '@angular/common';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { STColumn, STComponent } from '@delon/abc';
import { Teacher } from 'src/app/model/teacher.model';
import { GlobalService } from '@shared/service/global.service';
import { TeacherService } from '@shared/service/teacher.service';
import { EDIT_FLAG } from '@shared/constant/system.constant';

@Component({
    selector: 'app-teacher-view',
    templateUrl: './teacher-view.component.html',
})
export class TeacherViewComponent implements OnInit {
    pageHeader: string;
    teacher: Teacher = { editFlag: EDIT_FLAG.NEW, salaryConfigList: [] };
    @ViewChild('st', { static: true })
    st: STComponent;
    constructor(
        private globalService: GlobalService,
        private teacherService: TeacherService,
        private fb: FormBuilder,
        private activaterRouter: ActivatedRoute,
        public msgSrv: NzMessageService,
        public http: _HttpClient,
        private _location: Location
    ) {
    }

    editIndex = -1;
    editObj = {};
    form: FormGroup;
    courseTypeList = this.globalService.COURSE_TYPE_LIST;
    courseSubjectList = this.globalService.COURSE_SUBJECT_LIST;
    campusList = this.globalService.CAMPUS_LIST;
    gradeList = this.globalService.GRADE_LIST;
    classroomList = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];
    coursesColumns: STColumn[] = [
        { title: '名称', index: 'courseName' },
        { title: '校区', index: 'campus' },
        { title: '教室', index: 'classroom' },
        { title: '上课时间', index: 'startTime' },
        { title: '课程时长', index: 'duration' }
    ];

    ngOnInit() {
        this.form = this.fb.group({
            id: [null, []],
            name: [null, [Validators.required]],
            sex: [null, [Validators.required]],
            school: [null, []],
            birthday: [null, []],
            remark: [null, []],
            phone: [null, [Validators.required]],
            editFlag: [EDIT_FLAG.NO_CHANGE, []],
            teacherSalaryConfigList: this.fb.array([])
        });
        let teacherStr = this.activaterRouter.snapshot.params.teacher;
        if (teacherStr) {
            this.teacher = JSON.parse(teacherStr);
            this.teacher.editFlag = EDIT_FLAG.UPDATE;
            this.pageHeader = `教师信息编辑 -- [${this.teacher.name}]`;
        }
        this.form.patchValue(this.teacher);

        this.teacher.salaryConfigList.forEach(i => {
            i.editFlag = EDIT_FLAG.NO_CHANGE;
            const field = this.createSalary();
            field.patchValue(i);
            this.teacherSalaryConfigList.push(field);
        });
    }

    createSalary(): FormGroup {
        return this.fb.group({
            id: [null, []],
            grade: [null, [Validators.required]],
            courseSubject: [null, [Validators.required]],
            courseType: [null, [Validators.required]],
            teacherId: [this.teacher.id, []],
            unitPrice: [0, [Validators.required]],
            remark: ["", []],
            editFlag: [EDIT_FLAG.NEW, [Validators.required]]
        });
    }

    //#region get form fields
    get teacherSalaryConfigList() {
        return this.form.controls.teacherSalaryConfigList as FormArray;
    }
    //#endregion

    addSalary() {
        this.teacherSalaryConfigList.push(this.createSalary());
        this.editSalary(this.teacherSalaryConfigList.length - 1);
    }

    delSalary(index: number) {
        let salaryObj = this.teacherSalaryConfigList.at(index);
        if (salaryObj.value.editFlag == EDIT_FLAG.NEW) {
            this.teacherSalaryConfigList.removeAt(index);
        }
        else {
            salaryObj.value.editFlag = EDIT_FLAG.DELETE;
        }
    }

    editSalary(index: number) {
        if (this.editIndex !== -1 && this.editObj) {
            this.teacherSalaryConfigList.at(this.editIndex).patchValue(this.editObj);
        }
        this.editObj = { ...this.teacherSalaryConfigList.at(index).value };
        this.editIndex = index;
    }

    saveSalary(index: number) {
        let salaryObj = this.teacherSalaryConfigList.at(index);
        salaryObj.markAsDirty();
        if (salaryObj.invalid) return;
        if (salaryObj.value.editFlag == EDIT_FLAG.NO_CHANGE) {
            salaryObj.value.editFlag = EDIT_FLAG.UPDATE;
        }
        this.editIndex = -1;
    }

    cancelSalary(index: number) {
        let salaryObj = this.teacherSalaryConfigList.at(index);
        if (!salaryObj.value.id) {
            this.delSalary(index);
        } else {
            salaryObj.patchValue(this.editObj);
        }
        this.editIndex = -1;
    }

    _submitForm() {
        Object.keys(this.form.controls).forEach(key => {
            this.form.controls[key].markAsDirty();
            this.form.controls[key].updateValueAndValidity();
        });
        if (this.form.invalid) return;

        this.teacherService.saveTeacher(this.form.value).subscribe((res) => {
            this.goBack();
        });
    }

    goBack() {
        this._location.back();
    }

}
