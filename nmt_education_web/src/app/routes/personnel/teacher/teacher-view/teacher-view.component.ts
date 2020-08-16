import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { Location, DatePipe } from '@angular/common';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute, Router } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { STColumn, STComponent, STData, STChange } from '@delon/abc';
import { Teacher } from 'src/app/model/teacher.model';
import { GlobalService } from '@shared/service/global.service';
import { TeacherService } from '@shared/service/teacher.service';
import { EDIT_FLAG } from '@shared/constant/system.constant';
import { tap } from 'rxjs/operators';
import { AppContextService } from '@shared/service/appcontext.service';
import { ResponseData, TeacherCourseQueryParam } from 'src/app/model/system.model';

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
        public appCtx: AppContextService,
        private fb: FormBuilder,
        private activaterRouter: ActivatedRoute,
        private modalSrv: NzModalService,
        private router: Router,
        private _location: Location,
        private cdr: ChangeDetectorRef
    ) {
    }
    loading: boolean = false;
    editIndex = -1;
    editObj = {};
    form: FormGroup;
    courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
    courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
    campusList = this.appCtx.globalService.CAMPUS_LIST;
    gradeList = this.appCtx.globalService.GRADE_LIST;
    classroomList = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];
    coursesColumns: STColumn[] = [
        { title: '课程编号', index: 'code' },
      { title: '课程名称', index: 'name' },
        { title: '校区', index: 'campus',render: "campus" },
        // { title: '教室', index: 'classroom' },
        { title: '开始日期', index: 'startDate', render: "startDate" },
        { title: '课程时长(分钟)', index: 'perTime' }
    ];

    ngOnInit() {
        this.form = this.fb.group({
            id: [null, []],
            name: [null, [Validators.required]],
            sex: [null, [Validators.required]],
            school: [null, []],
            birthday: [null, []],
            remark: [null, []],
            // phone: [null, [Validators.required]],
            phone: [null, []],
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
        this.loading = true;
        this.appCtx.teacherService.saveTeacher(this.form.value)
            .pipe(
                tap(() => (this.loading = false))
            ).subscribe((res) => {
                this.modalSrv.success({
                    nzTitle: '处理结果',
                    nzContent: '教师信息保存成功！',
                    nzOnOk: () => {
                        this.router.navigate(["/personnel/teacher/list"]);
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
    courseQueryParam: TeacherCourseQueryParam = { pageNo: 1, pageSize: 10 };
    courses: ResponseData = { list: [], total: 0 };
    onSelectedTabChanged(event: any) {
        if (!this.courseLoaded && event.index == 1) {
            this.getScheduleCourses();
        }
    }

    getScheduleCourses() {
        if (!this.teacher.id) {
            return;
        }
        this.loading = true;
        this.courseQueryParam.teacherId = this.teacher.id;
        this.appCtx.teacherService.getCourseList(this.courseQueryParam)
            .pipe(
                tap(() => (this.loading = false)),
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
                this.getScheduleCourses();
                break;
        }
    }
}
