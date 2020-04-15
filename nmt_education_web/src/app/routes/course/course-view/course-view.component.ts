import { Component, OnInit, TemplateRef } from '@angular/core';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { COURSE_STATUS } from '@shared/constant/system.constant';
import { Course, CourseSession } from 'src/app/model/course.model';
import { Session } from 'protractor';
import { GlobalService } from '@shared/service/global.service';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
})
export class CourseViewComponent implements OnInit {
  pageHeader: string;
  course: Course = {};
  sessionParam: any = {
    startDate: new Date(),
    count: 0,
    startTime: new Date("2020-01-01 00:00:00"),
    duration: 0,
    dayOfWeek: [{ label: '星期天', value: 0 }, { label: '星期一', value: 1 }, { label: '星期二', value: 2 },
    { label: '星期三', value: 3 }, { label: '星期四', value: 4 }, { label: '星期五', value: 5 }, { label: '星期六', value: 6 }],
    price: 0,
    teacher: null
  };

  constructor(
    private globalService: GlobalService,
    private fb: FormBuilder,
    private routerinfo: ActivatedRoute,
    public msgSrv: NzMessageService,
    private modalSrv: NzModalService,
    public http: _HttpClient
  ) {

  }

  editSessionIndex = -1;
  editSessionObj = {};
  editFeeIndex = -1;
  editFeeObj = {};
  form: FormGroup;
  seasonList = this.globalService.SEASON_LIST;
  courseTypeList = this.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.globalService.COURSE_SUBJECT_LIST;
  courseStatusList = this.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.globalService.FEE_TYPE_LIST;
  gradeList = this.globalService.GRADE_LIST;
  campusList = this.globalService.CAMPUS_LIST;
  teacherList: any[] = [{ value: 'myk', label: '毛永康' }, { value: 'czy', label: '陈志毅' }, { value: 'drw', label: '杜任伟' }];
  classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];

  ngOnInit() {
    this.course.code = this.routerinfo.snapshot.params['courseno'];
    this.http.get(`/course/${this.course.code}`).subscribe(res => this.course = res);
    this.pageHeader = `课程编辑 [${this.course.code}]`;
    this.form = this.fb.group({
      name: [null, [Validators.required]],
      year: [null, []],
      season: [null, []],
      type: [null, [Validators.required]],
      startDate: [null, [Validators.required]],
      status: [1, [Validators.required]],
      teacher: ['myk', [Validators.required]],
      subject: [null, [Validators.required]],
      price: [null, [Validators.required]],
      campus: [null, [Validators.required]],
      grade: [null, [Validators.required]],
      classroom: [null, [Validators.required]],
      sessions: this.fb.array([]),
      feeList: this.fb.array([])
    });
    const sessionList: CourseSession[] = [
      {
        id: "1",
        teacher: "czy",
        duration: 90,
        startDateTime: new Date('2020-03-10 14:00:00'),
        price: 200
      },
      {
        id: '2',
        teacher: "czy",
        duration: 90,
        startDateTime: new Date('2020-03-17 14:00:00'),
        price: 100
      },
      {
        id: '3',
        teacher: "czy",
        duration: 90,
        startDateTime: new Date('2020-03-24 14:00:00'),
        price: 100
      },
    ];
    sessionList.forEach(i => {
      const field = this.createSession();
      field.patchValue(i);
      this.sessions.push(field);
    });

    const feeList = [
      {
        key: '1',
        feeType: 2,
        price: 200,
      },
      {
        key: '2',
        feeType: 3,
        price: 100,
      }];
    feeList.forEach(i => {
      const field = this.createFee();
      field.patchValue(i);
      this.feeList.push(field);
    });
  }

  createSession(): FormGroup {
    return this.fb.group({
      id: [null],
      teacher: [null, [Validators.required]],
      startDateTime: [null, [Validators.required]],
      duration: [null, [Validators.required]],
      price: [null, [Validators.required]],
    });
  }


  createFee(): FormGroup {
    return this.fb.group({
      key: [null],
      feeType: [null, [Validators.required]],
      price: [null, [Validators.required]],
    });
  }

  //#region get form fields
  get name() {
    return this.form.controls.name;
  }
  get year() {
    return this.form.controls.year;
  }
  get season() {
    return this.form.controls.season;
  }
  get type() {
    return this.form.controls.type;
  }
  get startDate() {
    return this.form.controls.startDate;
  }
  get status() {
    return this.form.controls.status;
  }
  get teacher() {
    return this.form.controls.teacher;
  }
  get subject() {
    return this.form.controls.subject;
  }
  get price() {
    return this.form.controls.price;
  }
  get campus() {
    return this.form.controls.campus;
  }
  get grade() {
    return this.form.controls.grade;
  }
  get classroom() {
    return this.form.controls.classroom;
  }

  get sessions() {
    return this.form.controls.sessions as FormArray;
  }

  get feeList() {
    return this.form.controls.feeList as FormArray;
  }
  //#endregion

  // add() {
  //   this.sessions.push(this.createSession());
  //   this.edit(this.sessions.length - 1);
  // }

  add(tpl: TemplateRef<{}>) {
    this.modalSrv.create({
      nzTitle: '创建课时',
      nzContent: tpl,
      nzWidth: 350,
      nzOnOk: () => {
        for (let i = 0; i < this.sessionParam.count;) {
          let day = this.sessionParam.startDate.getDay();
          if (this.sessionParam.dayOfWeek.find(d => { return d.checked && d.value == day; })) {
            let newSession: CourseSession = {};
            newSession.id = (this.sessions.length + 1).toString();
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
    this.feeList.at(index).markAsDirty();
    if (this.feeList.at(index).invalid) return;
    this.editFeeIndex = -1;
  }

  cancelFee(index: number) {
    if (!this.feeList.at(index).value.key) {
      this.delFee(index);
    } else {
      this.feeList.at(index).patchValue(this.editFeeObj);
    }
    this.editFeeIndex = -1;
  }

  _submitForm() {
    Object.keys(this.form.controls).forEach(key => {
      this.form.controls[key].markAsDirty();
      this.form.controls[key].updateValueAndValidity();
    });
    if (this.form.invalid) return;
  }

  getTeacherName(code: string) {
    return this.teacherList.find(t => { return t.value == code; }).label;
  }
}
