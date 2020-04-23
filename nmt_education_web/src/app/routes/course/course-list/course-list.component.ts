import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap, map } from 'rxjs/operators';
import { STComponent, STColumn, STData, STChange } from '@delon/abc';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from 'src/app/model/course.model';
import { GlobalService } from '@shared/service/global.service';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
})
export class CourseListComponent implements OnInit {
  courseDate = new FormGroup({
    courseDate: new FormControl()
  });
  q: any = {
    pi: 1,
    ps: 10,
    sorter: '',
    courseNo: '',
    status: null,
    campus: null,
    startDate: '',
  };
  data: any[] = [];
  loading = false;
  courseStatusList = this.globalService.COURSE_STATUS_LIST;
  campusList = this.globalService.CAMPUS_LIST;

  @ViewChild('st', { static: true })
  st: STComponent;
  columns: STColumn[] = [
    { title: '', index: 'key', type: 'checkbox' },
    { title: '课程编号', index: 'code' },
    { title: '名称', index: 'name' },
    { title: '校区', index: 'campus', render: "courseCampus" },
    {
      title: '状态',
      index: 'status',
      render: 'courseStatus'
    },
    {
      title: '开课时间',
      index: 'startDate',
      type: 'date',
      sort: {
        compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
      },
    },
    {
      title: '操作',
      buttons: [
        {
          text: '编辑',
          click: (item: any) => this.router.navigate([`/course/view/${item.courseNo}`]),
        },
        {
          text: '报名',
          click: (item: any) => this.msg.success(`报名${item.courseName}`),
        },
      ],
    },
  ];
  selectedRows: STData[] = [];
  description = '';
  totalCallNo = 0;
  expandForm = false;

  constructor(
    private globalService: GlobalService,
    private router: Router,
    private http: _HttpClient,
    public msg: NzMessageService,
    private modalSrv: NzModalService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.getData();
  }

  getData() {
    this.loading = true;
    this.http
      .get('/course', this.q)
      .pipe(
        map((list: Course[]) =>
          list.map(i => {
            i.statusDetail = this.globalService.COURSE_STATUS_LIST[i.status];
            //i.statusText = statusItem.text;
            //i.statusType = statusItem.type;
            return i;
          }),
        ),
        tap(() => (this.loading = false)),
      )
      .subscribe(res => {
        this.data = res;
        this.cdr.detectChanges();
      });
  }

  stChange(e: STChange) {
    switch (e.type) {
      case 'checkbox':
        this.selectedRows = e.checkbox!;
        this.totalCallNo = this.selectedRows.reduce((total, cv) => total + cv.callNo, 0);
        this.cdr.detectChanges();
        break;
      case 'filter':
        this.getData();
        break;
    }
  }

  remove() {
    this.http.delete('/course', { nos: this.selectedRows.map(i => i.no).join(',') }).subscribe(() => {
      this.getData();
      this.st.clearCheck();
    });
  }

  approval() {
    this.msg.success(`审批了 ${this.selectedRows.length} 笔`);
  }

  addNewCourse() {
    this.router.navigate([`/course/create`]);
}


  reset() {
    // wait form reset updated finished
    setTimeout(() => this.getData());
  }
}
