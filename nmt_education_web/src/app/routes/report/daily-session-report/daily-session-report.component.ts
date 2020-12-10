import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap } from 'rxjs/operators';
import { STChange, STColumn, STComponent, STData } from '@delon/abc';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AppContextService } from '@shared/service/appcontext.service';
import { ResponseData, RegisterSummaryQueryParam } from 'src/app/model/system.model';
import { DatePipe } from '@angular/common';
import { SIGNIN } from '@shared/constant/system.constant';

@Component({
  selector: 'daily-session-report',
  templateUrl: './daily-session-report.component.html',
})
export class DailySessionReportComponent implements OnInit {
  courseDate = new FormGroup({
    courseDate: new FormControl()
  });
  year: Date;
  startDate: Date[] = [new Date(), new Date()];
  registerDate: Date[];
  loading = false;
  total = 0;
  signInCount = 0;
  unSignInCount = 0;
  // pager = {
  //   front: false
  // };
  queryParam: RegisterSummaryQueryParam = { pageNo: 1, pageSize: 0 };
  data: [];//ResponseData = { list: [], total: 0 };
  courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.appCtx.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.appCtx.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;
  signInList = this.appCtx.globalService.SIGNIN_STATUS_LIST;
  seasonList = this.appCtx.globalService.SEASON_LIST;

  @ViewChild('st', { static: true })
  st: STComponent;
  columns: STColumn[] = [
    { title: '订单编号', index: 'registrationNumber' },
    {
      title: '报名时间', index: 'registerTime', type: 'date',
      sort: {
        compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
      }
    },
    { title: '报名类型', index: 'registrationType', render: "registrationType" },
    { title: '订单状态', index: 'registrationStatus' , render: "registrationStatus"},
    { title: '订单总额', index: 'totalAmount' },
    { title: '费用状态', index: 'feeStatus', render: "feeStatus" },
    { title: '课程名称', index: 'courseName' },
    { title: '学生姓名', index: 'studentName' },
    { title: '签到状态', index: 'signIn' , render: "signIn"}
  ];
  selectedRows: STData[] = [];
  description = '';
  totalCallNo = 0;
  expandForm = false;

  constructor(
    private datePipe: DatePipe,
    public appCtx: AppContextService,
    private router: Router,
    private http: _HttpClient,
    public msg: NzMessageService,
    private modalSrv: NzModalService,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit() {
    this.queryParam.startDate = this.datePipe.transform(this.startDate[0], 'yyyy-MM-dd');
    this.queryParam.endDate = this.datePipe.transform(this.startDate[1], 'yyyy-MM-dd');
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
    this.appCtx.courseService.registerSummary(this.queryParam)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe((res: ResponseData) => {
        res.list = res.list || [];
        res.list.forEach(element => {
          element.statusDetail = this.appCtx.globalService.getOrderStatus(element.registrationStatus);
        });
        this.data = res.list;
        this.total = res.list.length;
        this.signInCount = this.data.filter((d: any) => { return d.signIn == SIGNIN.SIGNIN; }).length;
        this.unSignInCount = this.total - this.signInCount;
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
        // this.queryParam.pageNo = e.pi;
        // this.getData();
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
    this.queryParam.registerStartDate = null;
    this.queryParam.registerEndDate = null;
    this.queryParam.season = null;
    this.queryParam.year = null;
    setTimeout(() => this.getData());
  }

  onDateRangeChanged(result: Date): void {
    this.queryParam.startDate = this.datePipe.transform(result[0], 'yyyy-MM-dd');
    this.queryParam.endDate = this.datePipe.transform(result[1], 'yyyy-MM-dd');
  }

  onRegisterDateRangeChanged(result: Date): void {
    this.queryParam.registerStartDate = this.datePipe.transform(result[0], 'yyyy-MM-dd');
    this.queryParam.registerEndDate = this.datePipe.transform(result[1], 'yyyy-MM-dd');
  }

}
