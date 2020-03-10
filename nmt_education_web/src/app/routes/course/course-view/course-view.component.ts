import { Component, OnInit } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
})
export class CourseViewComponent implements OnInit {
  pageHeader: string;
  course: any = {};
  i: any;

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
  typeList: any[] = [{ value: 'CHN', label: '语文' }, { value: 'MAT', label: '数学' }, { value: 'ENG', label: '英语' }];
  courseStatus: any[] = [{ index: 0, text: '未开始', value: false, type: 'default', checked: false },
  { index: 1, text: '开课中', value: false, type: 'processing', checked: false, },
  { index: 2, text: '已结课', value: false, type: 'success', checked: false }];
  teacherList: any[] = [{ value: 'myk', label: '毛永康' }, { value: 'czy', label: '陈志毅' }, { value: 'drw', label: '杜任伟' }];
  campousList: any[] = [{ value: 'sspu', label: '二工大' }, { value: 'shanda', label: '杉达' }, { value: 'jinrong', label: '金融学院' }];
  gradeList: any[] = [{ value: '1', label: '一年级' }, { value: '2', label: '二年级' }, { value: '3', label: '三年级' }];
  classroomList: any[] = [{ value: '101', label: '101' }, { value: '202', label: '202' }, { value: '303', label: '303' }];

  ngOnInit() {
    this.course.courseNo = this.routerinfo.snapshot.params['courseno'];
    this.http.get(`/course/${this.course.courseNo}`).subscribe(res => this.i = res);
    this.pageHeader = `课程编辑 [${this.course.courseNo}]`;
    this.form = this.fb.group({
      name: [null, [Validators.required]],
      year: [null, []],
      season: [null, []],
      type: [null, [Validators.required]],
      startDate: [null, [Validators.required]],
      status: [1, [Validators.required]],
      teacher: ['myk', [Validators.required]],
      duration: [null, [Validators.required]],
      price: [null, [Validators.required]],
      campous: [null, [Validators.required]],
      grade: [null, [Validators.required]],
      classroom: [null, [Validators.required]],
      sessions: this.fb.array([]),
    });
    const userList = [
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
    userList.forEach(i => {
      const field = this.createUser();
      field.patchValue(i);
      this.sessions.push(field);
    });
  }

  createUser(): FormGroup {
    return this.fb.group({
      key: [null],
      teacher: [null, [Validators.required]],
      dateTime: [null, [Validators.required]],
      classroom: [null, [Validators.required]],
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
  get duration() {
    return this.form.controls.duration;
  }
  get price() {
    return this.form.controls.price;
  }
  get campous() {
    return this.form.controls.campous;
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
  //#endregion

  add() {
    this.sessions.push(this.createUser());
    this.edit(this.sessions.length - 1);
  }

  del(index: number) {
    this.sessions.removeAt(index);
  }

  edit(index: number) {
    if (this.editIndex !== -1 && this.editObj) {
      this.sessions.at(this.editIndex).patchValue(this.editObj);
    }
    this.editObj = { ...this.sessions.at(index).value };
    this.editIndex = index;
  }

  save(index: number) {
    this.sessions.at(index).markAsDirty();
    if (this.sessions.at(index).invalid) return;
    this.editIndex = -1;
  }

  cancel(index: number) {
    if (!this.sessions.at(index).value.key) {
      this.del(index);
    } else {
      this.sessions.at(index).patchValue(this.editObj);
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
