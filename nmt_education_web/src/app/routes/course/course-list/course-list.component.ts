import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap } from 'rxjs/operators';
import { STChange, STColumn, STComponent, STData } from '@delon/abc';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AppContextService } from '@shared/service/appcontext.service';
import { CourseQueryParam, ResponseData } from 'src/app/model/system.model';
import { DatePipe } from '@angular/common';
import { Course } from 'src/app/model/course.model';
import { COURSE_STATUS } from '@shared/constant/system.constant';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
})
export class CourseListComponent implements OnInit {
  courseDate = new FormGroup({
    courseDate: new FormControl()
  });
  searchDate: Date[];
  year: Date;
  loading = false;
  pager = {
    front: false
  };
  queryParam: CourseQueryParam = { pageNo: 1, pageSize: 25 };
  data: ResponseData = { list: [], total: 0 };
  courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.appCtx.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.appCtx.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;
  campusList = this.appCtx.globalService.CAMPUS_LIST;
  seasonList = this.appCtx.globalService.SEASON_LIST;


  @ViewChild('st', { static: true })
  st: STComponent;
  columns: STColumn[] = [
    { title: '', index: 'key', type: 'checkbox' },
    // { title: '课程编号', index: 'code' },
    { title: '名称', index: 'name' },
    // { title: '课程科目', index: 'courseSubject', render: "courseSubject" },
    { title: '报名人数', index: 'registerNum', render: "registerNum" },
    { title: '课程类型', index: 'courseType', render: "courseType" },
    { title: '校区', index: 'campus', render: "courseCampus" },
    { title: '进度', index: 'progress', render: "progress" },
    {
      title: '状态',
      index: 'courseStatus',
      render: 'courseStatus'
    },
    {
      title: '开课日期',
      index: 'startDate',
      // type: 'date',
      render: 'startDate',
      sort: {
        compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
      },
    },
    {
      title: '上课时间',
      index: 'courseRegular',
    },
    {
      title: '操作',
      buttons: [
        {
          text: (item: any) => item.courseStatus != COURSE_STATUS.COMPLETE ? '编辑' : '查看',
          click: (item: any) => this.router.navigate([`/course/edit/${item.id}`]),
        },
        // {
        //   text: '签到',
        //   iif: (item: any) => item.courseStatus != COURSE_STATUS.COMPLETE,
        //   click: (item: any) => this.router.navigate([`/course/signsession/${item.id}`]),
        // },
        {
          text: '结课',
          iif: (item: any) => item.courseStatus != COURSE_STATUS.COMPLETE,
          click: (item: any) => this.finishCourse(item)
        }
      ],
    },
  ];
  selectedRows: STData[] = [];
  description = '';
  totalCallNo = 0;
  expandForm = false;

  constructor(
    public datePipe: DatePipe,
    public appCtx: AppContextService,
    private router: Router,
    private http: _HttpClient,
    public msg: NzMessageService,
    private modalSrv: NzModalService,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit() {
    this.queryParam = this.appCtx.storageService.get("CourseQuery") || this.queryParam;
    this.getData();
  }

  startQueryData() {
    this.queryParam.pageNo = 1;
    this.getData();
  }

  getData() {
    this.loading = true;
    if(this.year != null){
      this.queryParam.year = new Date(this.year).getFullYear();
    }

    this.appCtx.courseService.queryCourses(this.queryParam)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe((res: ResponseData) => {
        res.list = res.list || [];
        res.list.forEach(element => {
          element.statusDetail = this.appCtx.globalService.COURSE_STATUS_LIST[element.courseStatus];
        });
        this.data = res;
        this.cdr.detectChanges();
        this.appCtx.storageService.set("CourseQuery", this.queryParam);
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
    this.queryParam.year = null;
    this.queryParam.season = null;
    this.queryParam.pageNo = 1;
    setTimeout(() => this.getData());
  }

  onDateRangeChanged(result: Date): void {
    this.queryParam.startDate = this.datePipe.transform(result[0], 'yyyy-MM-dd');
    this.queryParam.endDate = this.datePipe.transform(result[1], 'yyyy-MM-dd');
  }

  addNewCourse() {
    this.router.navigate([`/course/create`]);
  }

  finishCourse(course: Course) {
    this.modalSrv.confirm({
      nzTitle: "结课确认",
      nzContent: `是否确认结课[${course.name}]? 结课后，课程信息不可再编辑/签到，未消耗课时将计入学生账户余额。`,
      nzOnOk: () => {
        this.loading = true;
        this.appCtx.courseService.finishCourse(course.id).pipe(
          tap(() => { this.loading = false; }, () => { this.loading = false; })
        )
          .subscribe((res: ResponseData) => {
            this.getData()
          });;
      }
    });
  }
  signInTableExport() {
    this.loading = true;
    if(this.year != null){
      this.queryParam.year = new Date(this.year).getFullYear();
    }

    this.appCtx.reportService.signInTable(this.queryParam)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe(((data) => {
        const link = document.createElement('a');
        const blob = new Blob([data], {type: 'application/vnd.ms-excel'});
        link.setAttribute('href', window.URL.createObjectURL(blob));
        var date = new Date();
        link.setAttribute('download', '班级签到表'+date.toLocaleDateString()+'.xlsx');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }));
  }
}
