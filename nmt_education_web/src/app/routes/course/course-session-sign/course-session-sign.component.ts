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
  loading: boolean = true;
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

          this.loadSessionSignStudentList(this.selectedSession.id);

        });

    }

  }

  loadSessionSignStudentList(sessionId: number): void {
    if (!sessionId || sessionId <= 0) {
      this.loading = false;
      return;
    }
    this.loading = true;
    this.appCtx.courseService.getSessionStudents(sessionId)
      .pipe(
        tap(() => (this.loading = false)),
      ).subscribe((signStudent: any) => {
        this.sessionSignStatusList.clear();
        if (!signStudent) {
          signStudent = [];
        }
        signStudent.forEach(i => {
          i.editFlag = EDIT_FLAG.NO_CHANGE;
          const field = this.createSessionSign();
          field.patchValue(i);
          this.sessionSignStatusList.push(field);
        });
      });
  }

  createSessionSign(): FormGroup {
    return this.fb.group({
      courseId: [0, []],
      courseScheduleId: [0, []],
      registerSummaryId: [0, []],
      studentId: [0, []],
      studentName: ["", []],
      signIn: [SIGNIN.UNSIGNIN, []],
      editFlag: [EDIT_FLAG.NO_CHANGE, []]
    });
  }

  //#region get form fields
  get sessionSignStatusList() {
    return this.form.controls.sessionSignStatusList as FormArray;
  }
  //#endregion

  onCourseSessionChanged(session: CourseSession): void {
    this.selectedSession = session;
    this.loadSessionSignStudentList(this.selectedSession.id);
  }

  onSignInfoChanged(index: number): void {
    let signObj = this.sessionSignStatusList.at(index) as FormGroup;
    signObj.get("editFlag").setValue(EDIT_FLAG.UPDATE);
  }

  _submitForm() {
    this.loading = true;
    let signInStudentList = this.form.value.sessionSignStatusList;
    signInStudentList = signInStudentList.filter(s => { return s.editFlag == EDIT_FLAG.UPDATE; });
    this.appCtx.courseService.sessionStudentsSignIn(signInStudentList)
      .pipe(
        tap(() => (this.loading = false)),
      ).subscribe((res) => {
        //this.goBack();
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
