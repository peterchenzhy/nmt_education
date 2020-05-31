import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';
import {_HttpClient} from '@delon/theme';
import {tap} from 'rxjs/operators';
import {STChange, STColumn, STComponent, STData} from '@delon/abc';
import {FormControl, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {AppContextService} from '@shared/service/appcontext.service';
import {CourseQueryParam, ResponseData} from 'src/app/model/system.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
})
export class CourseListComponent implements OnInit {
  courseDate = new FormGroup({
    courseDate: new FormControl()
  });
  searchDate: Date[];
  loading = false;
  pager = {
    front: false
  };
  queryParam: CourseQueryParam = {pageNo: 1, pageSize: 10};
  data: ResponseData = {list: [], total: 0};
  courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.appCtx.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.appCtx.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;

  @ViewChild('st', {static: true})
  st: STComponent;
  columns: STColumn[] = [
    {title: '', index: 'key', type: 'checkbox'},
    {title: '课程编号', index: 'code'},
    {title: '名称', index: 'name'},
    {title: '课程科目', index: 'courseSubject', render: "courseSubject"},
    {title: '课程类型', index: 'courseType', render: "courseType"},
    {title: '校区', index: 'campus', render: "courseCampus"},
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
          click: (item: any) => this.router.navigate([`/course/edit/${item.id}`]),
        },
        {
          text: '签到',
          click: (item: any) => this.router.navigate([`/course/signsession/${item.id}`]),
        }
      ],
    },
  ];
  selectedRows: STData[] = [];
  description = '';
  totalCallNo = 0;
  expandForm = false;

  constructor(
    private datePipe: DatePipe,
    private appCtx: AppContextService,
    private router: Router,
    private http: _HttpClient,
    public msg: NzMessageService,
    private modalSrv: NzModalService,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit() {
    this.getData();
  }

  startQueryData() {
    this.queryParam.pageNo = 1;
    this.getData();
  }

  getData() {
    this.loading = true;
    this.appCtx.courseService.queryCourses(this.queryParam)
      .pipe(
        tap(() => (this.loading = false)),
      )
      .subscribe((res: ResponseData) => {
        res.list = res.list || [];
        res.list.forEach(element => {
          element.statusDetail = this.appCtx.globalService.COURSE_STATUS_LIST[element.courseStatus];
        });
        this.data = res;
        this.cdr.detectChanges();
      });
  }

  stChange(e: STChange) {
    switch (e.type) {
      case 'checkbox':
        this.selectedRows = e.checkbox!;
        this.cdr.detectChanges();
        break;
      case 'pi':
        this.queryParam.pageNo = e.pi;
        this.getData();
        break;
    }

  }

  reset() {
    // wait form reset updated finished
    this.queryParam.courseSubject = null;
    this.queryParam.courseType = null;
    this.queryParam.grade = null;
    this.queryParam.startDate = null;
    this.queryParam.endDate = null;
    this.queryParam.pageNo = 1;
    setTimeout(() => this.getData());
  }

  onDateRangeChanged(result: Date): void {
    this.queryParam.startDate = this.datePipe.transform(result[0],'yyyy-MM-dd');
    this.queryParam.endDate = this.datePipe.transform(result[1],'yyyy-MM-dd');
  }

  addNewCourse() {
    this.router.navigate([`/course/create`]);
  }

}
