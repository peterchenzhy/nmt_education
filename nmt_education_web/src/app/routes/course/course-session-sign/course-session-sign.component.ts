import { Component, OnInit, TemplateRef, ChangeDetectorRef, ViewChild } from '@angular/core';
import { Location, DatePipe } from '@angular/common';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { COURSE_STATUS, EDIT_FLAG, SIGNIN } from '@shared/constant/system.constant';
import { Course, CourseSession } from 'src/app/model/course.model';
import { GlobalService } from '@shared/service/global.service';
import { Teacher } from 'src/app/model/teacher.model';
import { CourseService } from '@shared/service/course.service';
import { AppContextService } from '@shared/service/appcontext.service';
import { ResponseData } from 'src/app/model/system.model';
import { toNumber } from 'ng-zorro-antd';
import { tap } from 'rxjs/operators';
import { STComponent, STColumn } from '@delon/abc';

@Component({
  selector: 'app-course-session-sign',
  templateUrl: './course-session-sign.component.html',
})
export class CourseSessionSignComponent implements OnInit {
  pageHeader: string = "课程课时签到";
  course: Course = { editFlag: EDIT_FLAG.NO_CHANGE, courseExpenseList: [], courseScheduleList: [] };
  selectedSession: CourseSession = {};
  form: FormGroup;

  constructor(
    private appCtx: AppContextService,
    private fb: FormBuilder,
    private activaterRouter: ActivatedRoute,
    public msgSrv: NzMessageService,
    private modalSrv: NzModalService,
    public http: _HttpClient,
    private cdr: ChangeDetectorRef,
    private _location: Location
  ) {

  }

  seasonList = this.appCtx.globalService.SEASON_LIST;
  courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.appCtx.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.appCtx.globalService.COURSE_STATUS_LIST;
  signInStatusList = this.appCtx.globalService.SIGNIN_STATUS_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;
  campusList = this.appCtx.globalService.CAMPUS_LIST;
  teacherList: Teacher[] = [];
  selectedTeacherList: Teacher[] = [];
  classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];

  ngOnInit() {
    this.form = this.fb.group({
      sessionSignStatusList: this.fb.array([])
    });
    //this.form.patchValue(this.course);
    let courseId = this.activaterRouter.snapshot.params.id;
    let sessionId = this.activaterRouter.snapshot.queryParams.sessionId;
    if (courseId) {
      this.appCtx.courseService.getCourseDetails(courseId)
        .subscribe(res => {
          this.course = res;
          if (this.course.teacher) {
            this.teacherList.push(this.course.teacher);
            this.selectedTeacherList.push(this.course.teacher);
          }
          //this.form.patchValue(this.course);
          if (sessionId) {
            this.selectedSession = this.course.courseScheduleList.find(s => { return s.id == sessionId; });
          }
          else {
            let currentDate = new Date();
            let completedSeesions = this.course.courseScheduleList.filter(s => { return new Date(s.courseDatetime) <= currentDate; });
            if (completedSeesions && completedSeesions.length > 0) {
              this.selectedSession = completedSeesions[completedSeesions.length - 1];
            }
          }

          [{ sessionId: 1, studentId: 1, studentName: "test", signIn: SIGNIN.UNSIGNIN, editFlag: EDIT_FLAG.NO_CHANGE }].forEach(i => {
            i.editFlag = EDIT_FLAG.NO_CHANGE;
            const field = this.createSessionSign();
            field.patchValue(i);
            this.sessionSignStatusList.push(field);
          });

        });

    }

  }

  createSessionSign(): FormGroup {
    return this.fb.group({
      sessionId: [0, []],
      studentId: [0, []],
      studentName: ["", []],
      signIn: [0, []],
      editFlag: [EDIT_FLAG.NO_CHANGE, []]
    });
  }

  //#region get form fields
  get sessionSignStatusList() {
    return this.form.controls.sessionSignStatusList as FormArray;
  }
  //#endregion

  _submitForm() {
    Object.keys(this.form.controls).forEach(key => {
      this.form.controls[key].markAsDirty();
      this.form.controls[key].updateValueAndValidity();
    });
    if (this.form.invalid) return;
    this.course = { ...this.form.value };
    this.course.year = new Date(this.course.year).getFullYear();
    this.course.courseScheduleList.filter(s => { return s.editFlag != EDIT_FLAG.DELETE; })
      .sort((a, b) => new Date(a.courseDatetime).getTime() - new Date(b.courseDatetime).getTime())
      .forEach((d, i) => {
        d.courseTimes = i + 1;
      });
    this.appCtx.courseService.saveCourse(this.course).subscribe((res) => {
      this.goBack();
    });
  }

  goBack() {
    this._location.back();
  }

  getTeacherName(id: number) {
    let teacher = this.selectedTeacherList.find(t => { return t.id == id; });
    return teacher ? teacher.name : "";
  }

}
