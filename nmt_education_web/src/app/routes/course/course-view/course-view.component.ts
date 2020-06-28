import { Component, OnInit, TemplateRef, ChangeDetectorRef, ViewChild } from '@angular/core';
import { Location, DatePipe } from '@angular/common';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute, Router } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { COURSE_STATUS, EDIT_FLAG } from '@shared/constant/system.constant';
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
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
})
export class CourseViewComponent implements OnInit {
  pageHeader: string = "课程信息编辑";
  course: Course = { editFlag: EDIT_FLAG.NEW, courseExpenseList: [], courseScheduleList: [] };
  editSessionIndex = -1;
  editSessionObj = {};
  editFeeIndex = -1;
  editFeeObj = {};
  form: FormGroup;
  sessionParam: any = { title: "新建课时" };

  constructor(
    public appCtx: AppContextService,
    private fb: FormBuilder,
    private activaterRouter: ActivatedRoute,
    public msgSrv: NzMessageService,
    private modalSrv: NzModalService,
    public http: _HttpClient,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private _location: Location
  ) {

  }

  seasonList = this.appCtx.globalService.SEASON_LIST;
  courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.appCtx.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.appCtx.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;
  campusList = this.appCtx.globalService.CAMPUS_LIST;
  teacherList: Teacher[] = [];
  selectedTeacherList: Teacher[] = [];
  classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];
  loading: boolean = false;
  ngOnInit() {
    this.form = this.fb.group({
      id: [0, []],
      code: [null, []],
      name: [null, [Validators.required]],
      description: [null, []],
      year: [null, [Validators.required]],
      season: [null, [Validators.required]],
      startDate: [null, [Validators.required]],
      endDate: [null, []],
      grade: [null, [Validators.required]],
      courseType: [null, [Validators.required]],
      courseSubject: [null, [Validators.required]],
      courseClassification: [null, [Validators.required]],
      courseRegular: [null, []],
      status: [COURSE_STATUS.PENDING, []],
      perTime: [0, [Validators.required]],
      teacherId: [null, []],
      campus: [null, [Validators.required]],
      classroom: [null, []],
      times: [0, []],
      totalStudent: [0, [Validators.required, Validators.min(1)]],
      remark: [null, []],
      editFlag: [EDIT_FLAG.NO_CHANGE, []],
      courseScheduleList: this.fb.array([]),
      courseExpenseList: this.fb.array([])
    });
    this.form.patchValue(this.course);
    let courseId = this.activaterRouter.snapshot.params.id;
    if (courseId) {
      this.loading = true;
      this.appCtx.courseService.getCourseDetails(courseId)
        .pipe(
          tap(() => (this.loading = false)),
        ).subscribe(res => {
          this.course = res;
          let year: number = toNumber(this.course.year.toString());
          this.course.year = new Date().setFullYear(year);
          this.course.editFlag = EDIT_FLAG.UPDATE;
          this.pageHeader = `课程信息编辑 [${this.course.name}--${this.course.code}]`;
          if (this.course.teacher) {
            this.teacherList.push(this.course.teacher);
            this.selectedTeacherList.push(this.course.teacher);
          }
          this.form.patchValue(this.course);
          this.course.courseScheduleList.forEach(i => {
            i.editFlag = EDIT_FLAG.NO_CHANGE;
            const field = this.createSession();
            field.patchValue(i);
            this.sessions.push(field);
          });

          this.course.courseExpenseList.forEach(i => {
            i.editFlag = EDIT_FLAG.NO_CHANGE;
            const field = this.createFee();
            field.patchValue(i);
            this.feeList.push(field);
          });

          let currentDate = new Date();
          let activeSessions = this.course.courseScheduleList.filter(s => { return s.editFlag != EDIT_FLAG.DELETE && new Date(s.courseDatetime) > currentDate; });
          if (activeSessions && activeSessions.length > 0) {
            this.sessionParam.title = "更换课时";
          }
        });

    }

  }

  get isEditCourse() {
    return this.form.value.editFlag != EDIT_FLAG.NEW;
  }

  createSession(): FormGroup {
    return this.fb.group({
      id: [0, []],
      courseId: [this.course.id, []],
      teacherId: [null, []],
      courseDatetime: [null, [Validators.required]],
      perTime: [null, [Validators.required]],
      teacherPrice: [0, [Validators.required]],
      courseTimes: [0, [Validators.required]],
      signIn: [0, []],
      editFlag: [EDIT_FLAG.NEW, []]
    });
  }


  createFee(): FormGroup {
    return this.fb.group({
      id: [0, []],
      courseId: [this.course.id, []],
      type: [null, [Validators.required]],
      price: [0, []],
      //price: [0, [Validators.required, Validators.min(1)]],
      editFlag: [EDIT_FLAG.NEW, []]
    });
  }

  //#region get form fields
  get sessions() {
    return this.form.controls.courseScheduleList as FormArray;
  }

  get feeList() {
    return this.form.controls.courseExpenseList as FormArray;
  }
  //#endregion

  // add() {
  //   this.sessions.push(this.createSession());
  //   this.edit(this.sessions.length - 1);
  // }

  addSessions(tpl: TemplateRef<{}>) {
    this.sessionParam = {
      title: "新建课时",
      startDate: new Date(),
      count: 0,
      startTime: new Date("2020-01-01 00:00:00"),
      perTime: this.form.value.perTime,
      dayOfWeek: [{ label: '星期日', value: 0 }, { label: '星期一', value: 1 }, { label: '星期二', value: 2 },
      { label: '星期三', value: 3 }, { label: '星期四', value: 4 }, { label: '星期五', value: 5 }, { label: '星期六', value: 6 }],
      price: 0,
      teacherId: this.form.value.teacherId
    };
    let currentDate = new Date();
    let activeSessions = this.sessions.value.filter(s => { return s.editFlag != EDIT_FLAG.DELETE && new Date(s.courseDatetime) > currentDate; });
    if (activeSessions && activeSessions.length > 0) {
      this.sessionParam.title = "更换课时";
      this.sessionParam.count = activeSessions.length;
      this.sessionParam.startDate = new Date(activeSessions[0].courseDatetime);
      this.sessionParam.startTime = new Date(activeSessions[0].courseDatetime);
      this.sessionParam.perTime = this.form.value.perTime;
      // this.sessionParam.teacherId = activeSessions[0].teacherId;
      activeSessions.forEach(s => {
        let day = new Date(s.courseDatetime).getDay();
        this.sessionParam.dayOfWeek.find(d => { return d.value == day; }).checked = true;
      });
    }

    let singleFee = this.feeList.value.find(f => { return f.type == 1 && f.editFlag != EDIT_FLAG.DELETE; });
    this.sessionParam.price = singleFee ? singleFee.price : 0;
    this.modalSrv.create({
      nzTitle: this.sessionParam.title,
      nzContent: tpl,
      nzWidth: 350,
      nzOnOk: () => {
        debugger;
        let currentDate = new Date();
        for (let i = 0; i < this.sessions.length;) {
          let session = this.sessions.at(i);
          if (session.value.editFlag != EDIT_FLAG.DELETE && new Date(session.value.courseDatetime) > currentDate) {
            if (this.sessionParam.count == 0) {
              if (session.value.editFlag == EDIT_FLAG.NEW) {
                this.sessions.removeAt(i);
              }
              else {
                session.value.editFlag = EDIT_FLAG.DELETE;
                i++;
              }
              continue;
            }
            else {
              for (let di = 0; di < 7; di++) {
                let day = this.sessionParam.startDate.getDay();
                if (this.sessionParam.dayOfWeek.find(d => { return d.checked && d.value == day; })) {
                  session.value.perTime = this.sessionParam.perTime;
                  session.value.teacherId = this.sessionParam.teacherId;
                  //session.value.teacherPrice = this.sessionParam.price;
                  let dateStr = this.sessionParam.startDate.toLocaleDateString();
                  let timeStr = this.sessionParam.startTime.toTimeString();
                  session.value.courseDatetime = new Date(dateStr + " " + timeStr);
                  if (session.value.editFlag != EDIT_FLAG.NEW) {
                    session.value.editFlag = EDIT_FLAG.UPDATE;
                  }
                  this.sessionParam.count--;
                  this.sessionParam.startDate.setDate(this.sessionParam.startDate.getDate() + 1);
                  break;
                }
                this.sessionParam.startDate.setDate(this.sessionParam.startDate.getDate() + 1);
              }
            }
            session.patchValue(session.value, { emitEvent: true });
            session.markAsDirty();
            i++;
          }
          else {
            i++;
          }
        }
        for (let i = 0; i < this.sessionParam.count;) {
          let day = this.sessionParam.startDate.getDay();
          if (this.sessionParam.dayOfWeek.find(d => { return d.checked && d.value == day; })) {
            let newSession: CourseSession = {};
            newSession.courseId = this.form.value.id;
            newSession.teacherId = this.sessionParam.teacherId;
            newSession.perTime = this.sessionParam.perTime;
            newSession.teacherPrice = this.sessionParam.price;
            let dateStr = this.sessionParam.startDate.toLocaleDateString();
            let timeStr = this.sessionParam.startTime.toTimeString();
            newSession.courseDatetime = new Date(dateStr + " " + timeStr);
            const sessionObj = this.createSession();
            sessionObj.patchValue(newSession);
            this.sessions.push(sessionObj);
            i++;
          }
          this.sessionParam.startDate.setDate(this.sessionParam.startDate.getDate() + 1);
        }
        let existsSessions = this.sessions.value.filter(s => { return s.editFlag != EDIT_FLAG.DELETE; });
        let courseStartDate = null;
        if (existsSessions.length > 0) {
          courseStartDate = existsSessions.sort((a, b) => new Date(a.courseDatetime).getTime() - new Date(b.courseDatetime).getTime())[0].courseDatetime;
        }
        let selectDays = this.sessionParam.dayOfWeek.filter(d => { return d.checked; });
        let regular = null;
        if (selectDays.length > 0) {
          let startTime = this.sessionParam.startTime.toTimeString().substring(0, 5);
          let regularTime = this.sessionParam.startTime.getMinutes() + this.sessionParam.perTime;
          let endTime = new Date(new Date(this.sessionParam.startTime).setMinutes(regularTime)).toTimeString().substring(0, 5);
          regular = "每周" + selectDays.map(d => d.label.replace("星期", "")).join("、") + " " + startTime + "-" + endTime;
        }

        this.form.patchValue({ times: existsSessions.length, startDate: courseStartDate, courseRegular: regular });
      },
    });
  }

  del(index: number) {
    let sessionObj = this.sessions.at(index);
    if (sessionObj.value.editFlag == EDIT_FLAG.NEW) {
      this.sessions.removeAt(index);
    }
    else {
      sessionObj.value.editFlag = EDIT_FLAG.DELETE;
    }
  }

  edit(index: number) {
    if (this.editSessionIndex !== -1 && this.editSessionObj) {
      this.sessions.at(this.editSessionIndex).patchValue(this.editSessionObj);
    }
    this.editSessionObj = { ...this.sessions.at(index).value };
    this.editSessionIndex = index;
  }

  save(index: number) {
    let sessionObj = this.sessions.at(index) as FormGroup;
    Object.keys(sessionObj.controls).forEach(key => {
      sessionObj.controls[key].markAsDirty();
      sessionObj.controls[key].updateValueAndValidity();
    });
    if (sessionObj.invalid) return;
    if (sessionObj.value.editFlag == EDIT_FLAG.NO_CHANGE) {
      sessionObj.value.editFlag = EDIT_FLAG.UPDATE;
    }
    this.editSessionIndex = -1;
  }

  cancel(index: number) {
    if (!this.sessions.at(index).value.id) {
      this.del(index);
    } else {
      this.sessions.at(index).patchValue(this.editSessionObj);
    }
    this.editSessionIndex = -1;
  }

  signIn(index: number) {
    let sessionObj = this.sessions.at(index) as FormGroup;
    this.router.navigate([`/course/signsession/${sessionObj.value.courseId}`], { queryParams: { sessionId: sessionObj.value.id } })
  }

  addFee() {
    this.feeList.push(this.createFee());
    this.editFee(this.feeList.length - 1);
  }

  delFee(index: number) {
    let feeObj = this.feeList.at(index);
    if (feeObj.value.editFlag == EDIT_FLAG.NEW) {
      this.feeList.removeAt(index);
    }
    else {
      feeObj.value.editFlag = EDIT_FLAG.DELETE;
    }
  }

  editFee(index: number) {
    if (this.editFeeIndex !== -1 && this.editFeeObj) {
      this.feeList.at(this.editFeeIndex).patchValue(this.editFeeObj);
    }
    this.editFeeObj = { ...this.feeList.at(index).value };
    this.editFeeIndex = index;
  }

  saveFee(index: number) {
    let feeObj = this.feeList.at(index) as FormGroup;
    Object.keys(feeObj.controls).forEach(key => {
      feeObj.controls[key].markAsDirty();
      feeObj.controls[key].updateValueAndValidity();
    });
    if (feeObj.invalid) return;
    if (feeObj.value.editFlag == EDIT_FLAG.NO_CHANGE) {
      feeObj.value.editFlag = EDIT_FLAG.UPDATE;
    }
    this.editFeeIndex = -1;
  }

  cancelFee(index: number) {
    let feeObj = this.feeList.at(index);
    if (!feeObj.value.id) {
      this.delFee(index);
    } else {
      feeObj.patchValue(this.editFeeObj);
    }
    this.editFeeIndex = -1;
  }

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
    this.loading = true;
    this.appCtx.courseService.saveCourse(this.course)
      .pipe(
        tap(() => (this.loading = false)),
      ).subscribe((res) => {
        this.modalSrv.success({
          nzTitle: '处理结果',
          nzContent: '课程信息保存成功！',
          nzOnOk: () => {
            this.router.navigate(["/course/list"]);
          }
        });
      });
  }

  goBack() {
    this._location.back();
  }
  getTeacherName(id: number) {
    let teacher = this.selectedTeacherList.find(t => { return t.id == id; });
    return teacher ? teacher.name : "";
  }

  nzFilterOption = () => true;
  searchTeacher(value: string): void {
    if (!value || value == "") {
      return;
    }
    this.appCtx.teacherService.fuzzyQueryTeachers(value).subscribe((data: Teacher[]) => {
      this.teacherList = data;
    });
  }

  coursePerTimeChanged() {
    this.updateSessionByCourseProperties();
  }

  teacherSelected(value: number) {
    if (value < 0) {
      return;
    }
    let selectedTeacher = this.teacherList.find(c => { return c.id == value; });
    this.selectedTeacherList.push(selectedTeacher);
    this.updateSessionByCourseProperties();
    //this.course.teacherId = this.teacherList.find(c => { return c.id == value; });
    // const payList: Payment[] = [
    //   {
    //     id: "1", type: 1, method: 4, status: 1,
    //     price: 1000, discount: 1, receivable: 400, paied: 400, comment: ""
    //   },
    //   {
    //     id: "2", type: 3, method: 4, status: 1, price: 1000,
    //     discount: 0.9, receivable: 900, deduction: 200, paied: 700, comment: ""
    //   }];
    // payList.forEach(i => {
    //   const field = this.createPay();
    //   field.patchValue(i);
    //   this.payList.push(field);
    // });
  }

  updateSessionByCourseProperties(): void {
    let currentDate = new Date();
    for (let i = 0; i < this.sessions.length; i++) {
      let session = this.sessions.at(i);
      if (session.value.editFlag == EDIT_FLAG.DELETE || new Date(session.value.courseDatetime) < currentDate) {
        return;
      }
      session.value.perTime = this.form.value.perTime;
      session.value.teacherId = this.form.value.teacherId;
      if (session.value.editFlag == EDIT_FLAG.NO_CHANGE) {
        session.value.editFlag = EDIT_FLAG.UPDATE;
      }
      session.patchValue(session.value, { emitEvent: true });
      session.markAsDirty();
    }
  }

  onSelectedTabChanged(event: any) {
    if (!this.studentLoaded && event.index == 1) {
      this.getRegisteredStudents();
    }
  }
  studentLoaded = false;
  studentList = [];
  @ViewChild('st', { static: true })
  st: STComponent;
  columns: STColumn[] = [
    { title: '序号', index: 'index' },
    { title: '学生姓名', index: 'name' },
    { title: '性别', index: 'sex', render: "sex" },
    { title: '学校', index: 'school' },
    { title: '年级', index: 'grade', render: "grade" },
    { title: '电话', index: 'phone' }];
  getRegisteredStudents() {
    if (!this.course.id) {
      return;
    }
    this.loading = true;
    this.appCtx.courseService.getRegisteredStudents(this.course.id)
      .pipe(
        tap(() => (this.loading = false)),
      )
      .subscribe((res: any) => {
        res = res || [];
        res.forEach((element, i) => {
          element.index = i + 1;
        });
        this.studentList = res;
        this.studentLoaded = true;
        this.cdr.detectChanges();
      });
  }
}
