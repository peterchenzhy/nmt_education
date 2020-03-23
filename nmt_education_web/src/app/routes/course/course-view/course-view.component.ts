import { Component, OnInit } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { COURSE_STATUS_LIST, COURSE_STATUS, COURSE_SUBJECT, COURSE_SUBJECT_LIST, GRADE_LIST, CAMPUS_LIST, SEASON_LIST, COURSE_TYPE_LIST, FEE_TYPE_LIST, getFeeTypeLabel } from '@shared/constant/system.constant';
import { Course } from 'src/app/model/course.model';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
})
export class CourseViewComponent implements OnInit {
  getFeeTypeLabel = getFeeTypeLabel;
  pageHeader: string;
  course: Course = {};

  constructor(
    private fb: FormBuilder,
    private routerinfo: ActivatedRoute,
    public msgSrv: NzMessageService,
    public http: _HttpClient
  ) {

  }

  editSessionIndex = -1;
  editSessionObj = {};
  editFeeIndex = -1;
  editFeeObj = {};
  form: FormGroup;
  seasonList = SEASON_LIST;
  courseTypeList = COURSE_TYPE_LIST;
  courseSubjectList = COURSE_SUBJECT_LIST;
  courseStatusList = COURSE_STATUS_LIST;
  feeTypeList = FEE_TYPE_LIST;
  gradeList = GRADE_LIST;
  campusList = CAMPUS_LIST;
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
    const sessionList = [
      {
        key: '1',
        classroom: '101',
        teacher: '陈志毅',
        dateTime: '2020-03-10 14:00:00',
        price: '200',
      },
      {
        key: '2',
        classroom: '101',
        teacher: '陈志毅',
        dateTime: '2020-03-17 14:00:00',
        price: '100',
      },
      {
        key: '3',
        classroom: '101',
        teacher: '陈志毅',
        dateTime: '2020-03-24 14:00:00',
        price: '100',
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
      key: [null],
      teacher: [null, [Validators.required]],
      dateTime: [null, [Validators.required]],
      classroom: [null, [Validators.required]],
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

  add() {
    this.sessions.push(this.createSession());
    this.edit(this.sessions.length - 1);
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

}
