import { Component, OnInit, TemplateRef } from '@angular/core';
import { Location } from '@angular/common';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { COURSE_STATUS, EDIT_FLAG } from '@shared/constant/system.constant';
import { Course, CourseSession } from 'src/app/model/course.model';
import { GlobalService } from '@shared/service/global.service';
import { Teacher } from 'src/app/model/teacher.model';
import { CourseService } from '@shared/service/course.service';
import { AppContextService } from '@shared/service/appcontext.service';
import { ResponseData } from 'src/app/model/system.model';

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
    private appCtx: AppContextService,
    private fb: FormBuilder,
    private activaterRouter: ActivatedRoute,
    public msgSrv: NzMessageService,
    private modalSrv: NzModalService,
    public http: _HttpClient,
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
  classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];

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
      status: [COURSE_STATUS.PENDING, []],
      perTime: [0, [Validators.required]],
      teacherId: [null, [Validators.required]],
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
      this.appCtx.courseService.getCourseDetails(courseId)
        .subscribe(res => {
          this.course = res;
          this.course.editFlag = EDIT_FLAG.UPDATE;
          this.pageHeader = `课程信息编辑 [${this.course.code}]`;
          this.teacherList.push(this.course.teacher);
          this.form.patchValue(this.course);
          this.course.courseScheduleList.forEach(i => {
            const field = this.createSession();
            field.patchValue(i);
            this.sessions.push(field);
          });

          this.course.courseExpenseList.forEach(i => {
            const field = this.createFee();
            field.patchValue(i);
            this.feeList.push(field);
          });
        });

    }

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
      dayOfWeek: [{ label: '星期天', value: 0 }, { label: '星期一', value: 1 }, { label: '星期二', value: 2 },
      { label: '星期三', value: 3 }, { label: '星期四', value: 4 }, { label: '星期五', value: 5 }, { label: '星期六', value: 6 }],
      price: 0,
      teacherId: this.form.value.teacherId
    };
    let currentDate = new Date();
    let activeSessions = this.sessions.value.filter(s => { return s.editFlag != EDIT_FLAG.DELETE && s.courseDatetime > currentDate; });
    if (activeSessions && activeSessions.length > 0) {
      this.sessionParam.title = "更换课时";
      this.sessionParam.count = activeSessions.length;
      this.sessionParam.startDate = activeSessions[0].courseDatetime;
      this.sessionParam.startTime = activeSessions[0].courseDatetime;
      this.sessionParam.perTime = this.form.value.perTime,
        this.sessionParam.teacherId = activeSessions[0].teacherId;
      activeSessions.forEach(s => {
        let day = s.courseDatetime.getDay();
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
        this.form.patchValue({ times: this.sessions.value.filter(s => { return s.editFlag != EDIT_FLAG.DELETE; }).length });
      },
    });
  }

  del(index: number) {
    this.sessions.removeAt(index);
  }

  edit(index: number) {
    if (this.editSessionIndex !== -1 && this.editSessionObj) {
      this.sessions.at(this.editSessionIndex).patchValue(this.editSessionObj);
    }
    this.editSessionObj = { ...this.sessions.at(index).value };
    this.editSessionIndex = index;
  }

  save(index: number) {
    this.sessions.at(index).markAsDirty();
    if (this.sessions.at(index).invalid) return;
    this.editSessionIndex = -1;
  }

  cancel(index: number) {
    if (!this.sessions.at(index).value.key) {
      this.del(index);
    } else {
      this.sessions.at(index).patchValue(this.editSessionObj);
    }
    this.editSessionIndex = -1;
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
    this.feeList.removeAt(index);
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
    this.course.courseScheduleList.filter(s => { return s.editFlag != EDIT_FLAG.DELETE; }).forEach((d, i) => {
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
    let teacher = this.teacherList.find(t => { return t.id == id; });
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

  teacherSelected(value: number) {
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
}
