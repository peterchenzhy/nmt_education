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
    private globalService: GlobalService,
    private courseService: CourseService,
    private fb: FormBuilder,
    private activaterRouter: ActivatedRoute,
    public msgSrv: NzMessageService,
    private modalSrv: NzModalService,
    public http: _HttpClient,
    private _location: Location
  ) {

  }

  seasonList = this.globalService.SEASON_LIST;
  courseTypeList = this.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.globalService.FEE_TYPE_LIST;
  gradeList = this.globalService.GRADE_LIST;
  campusList = this.globalService.CAMPUS_LIST;
  teacherList: Teacher[] = [];
  classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];

  ngOnInit() {
    this.form = this.fb.group({
      id: [null, []],
      code: [null, []],
      name: [null, [Validators.required]],
      description: [null, []],
      year: [null, [Validators.required]],
      season: [null, [Validators.required]],
      startDate: [null, [Validators.required]],
      endDate: [null, [Validators.required]],
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
    let courseStr = this.activaterRouter.snapshot.params.course;
    if (courseStr) {
      this.course = JSON.parse(courseStr);
      this.course.editFlag = EDIT_FLAG.UPDATE;
      this.pageHeader = `课程信息编辑 [${this.course.code}]`;
    }
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
  }

  createSession(): FormGroup {
    return this.fb.group({
      id: [null],
      courseId: [null, []],
      teacher: [null, []],
      startDateTime: [null, [Validators.required]],
      duration: [null, [Validators.required]],
      price: [0, [Validators.required]],
      editFlag: [EDIT_FLAG.NEW, []]
    });
  }


  createFee(): FormGroup {
    return this.fb.group({
      id: [null, []],
      courseId: [null, []],
      type: [null, [Validators.required]],
      price: [0, [Validators.required, Validators.min(1)]],
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
      duration: 0,
      dayOfWeek: [{ label: '星期天', value: 0 }, { label: '星期一', value: 1 }, { label: '星期二', value: 2 },
      { label: '星期三', value: 3 }, { label: '星期四', value: 4 }, { label: '星期五', value: 5 }, { label: '星期六', value: 6 }],
      price: 0,
      teacher: null
    };
    let currentDate = new Date();
    let activeSessions = this.sessions.value.filter(s => { return s.editFlag != EDIT_FLAG.DELETE && s.startDateTime > currentDate; });
    if (activeSessions && activeSessions.length > 0) {
      this.sessionParam.title = "更换课时";
      this.sessionParam.count = activeSessions.length;
      this.sessionParam.startDate = activeSessions[0].startDateTime;
      this.sessionParam.startTime = activeSessions[0].startDateTime;
      this.sessionParam.duration = activeSessions[0].duration;
      this.sessionParam.teacher = activeSessions[0].teacher;
      activeSessions.forEach(s => {
        let day = s.startDateTime.getDay();
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
            debugger;
            let newSession: CourseSession = {};
            newSession.courseId = this.course.id;
            newSession.teacher = this.sessionParam.teacher;
            newSession.duration = this.sessionParam.duration;
            newSession.price = this.sessionParam.price;
            let dateStr = this.sessionParam.startDate.toLocaleDateString();
            let timeStr = this.sessionParam.startTime.toTimeString();
            newSession.startDateTime = new Date(dateStr + " " + timeStr);
            const sessionObj = this.createSession();
            sessionObj.patchValue(newSession);
            this.sessions.push(sessionObj);
            i++;
          }
          this.sessionParam.startDate.setDate(this.sessionParam.startDate.getDate() + 1);
        }

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
    this.courseService.saveCourse(this.form.value).subscribe((res) => {
      this.goBack();
    });
  }

  goBack() {
    this._location.back();
  }
  getTeacherName(code: string) {
    let teacher = this.teacherList.find(t => { return t.code == code; });
    return teacher ? teacher.name : "";
  }
}
