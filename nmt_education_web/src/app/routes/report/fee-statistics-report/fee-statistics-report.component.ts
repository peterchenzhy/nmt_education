import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap } from 'rxjs/operators';
import { STChange, STColumn, STComponent, STData } from '@delon/abc';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AppContextService } from '@shared/service/appcontext.service';
import {ResponseData, FeeStatisticsQueryParam, FeeSummary} from 'src/app/model/system.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'fee-statistics-report',
  templateUrl: './fee-statistics-report.component.html',
})
export class FeeStatisticsReportComponent implements OnInit {
  courseDate = new FormGroup({
    courseDate: new FormControl()
  });
  startDate: Date[] = [];
  loading = false;
  total = 0;
  pager = {
    front: false
  };
  year: Date;
  queryParam: FeeStatisticsQueryParam = { pageNo: 1, pageSize: 25 };
  data: ResponseData = { list: [], total: 0 };
  summaryData : FeeSummary={registerStudentCount: 0 };
  campusList = this.appCtx.globalService.CAMPUS_LIST;
  seasonList = this.appCtx.globalService.SEASON_LIST;
  userList = this.appCtx.globalService.USER_LIST;
  feeFlowList = [{ label: "退费", value: 0 }, { label: "缴费", value: 1 }];

  @ViewChild('st', { static: true })
  st: STComponent;
  columns: STColumn[] = [
    { title: '订单编号', index: 'registrationNumber' },
    { title: '课程名称', index: 'courseName' },
    { title: '校区', index: 'campus', render: "courseCampus" },
    { title: '学生姓名', index: 'studentName' },
    { title: '支付金额', index: 'amount' },
    { title: '结余抵扣', index: 'accountAmount' },
    { title: '实际费用', index: 'actuallyAmount' },
    { title: '费用状态', index: 'feeFlowTypeStr' },
    { title: '支付方式', index: 'paymentStr' },
    { title: '操作人', index: 'userName' },
    {
      title: '支付时间', index: 'feeTime', type: 'date',
      sort: {
        compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
      }
    }
  ];

  selectedRows: STData[] = [];
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

    this.appCtx.reportService.queryFeeStatistics(this.queryParam,true)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe((res: ResponseData) => {
        res.list = res.list || [];
        this.data = res;
        this.cdr.detectChanges();
      });
    this.appCtx.reportService.queryFeeSummary(this.queryParam,true)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe((res: FeeSummary) => {
        this.summaryData = res;
        // res.list = res.list || [];
        // this.data = res;
        // this.cdr.detectChanges();
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
    this.queryParam.campus = null;
    this.queryParam.feeFlowType = null;
    this.queryParam.startDate = null;
    this.queryParam.endDate = null;
    this.queryParam.pageNo = 1;
    this.queryParam.season=null;
    this.queryParam.year=null;
    setTimeout(() => this.getData());
  }

  onDateRangeChanged(result: Date): void {
    this.queryParam.startDate = this.datePipe.transform(result[0], 'yyyy-MM-dd');
    this.queryParam.endDate = this.datePipe.transform(result[1], 'yyyy-MM-dd');
  }

  export() {
    this.loading = true;
    if(this.year != null){
      this.queryParam.year = new Date(this.year).getFullYear();
    }

    this.appCtx.reportService.exportFeeStatistics(this.queryParam)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe(((data) => {
        const link = document.createElement('a');
        const blob = new Blob([data], {type: 'application/vnd.ms-excel'});
        link.setAttribute('href', window.URL.createObjectURL(blob));
        var date = new Date();
        link.setAttribute('download', '费用统计报表'+date.toLocaleDateString()+'.xlsx');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }));
  }

  export2() {
    this.loading = true;
    if(this.year != null){
      this.queryParam.year = new Date(this.year).getFullYear();
    }
    this.appCtx.reportService.exportScheduleTeacher(this.queryParam)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe(((data) => {
        const link = document.createElement('a');
        const blob = new Blob([data], {type: 'application/vnd.ms-excel'});
        link.setAttribute('href', window.URL.createObjectURL(blob));
        var date = new Date();
        link.setAttribute('download', '课程明细统计'+date.toLocaleDateString()+'.xlsx');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }));
  }
  export3() {
    this.loading = true;
    if(this.year != null){
      this.queryParam.year = new Date(this.year).getFullYear();
    }

    this.appCtx.reportService.exportTeacherSalary(this.queryParam)
      .pipe(
        tap(() => { this.loading = false; }, () => { this.loading = false; })
      )
      .subscribe(((data) => {
        const link = document.createElement('a');
        const blob = new Blob([data], {type: 'application/vnd.ms-excel'});
        link.setAttribute('href', window.URL.createObjectURL(blob));
        var date = new Date();
        link.setAttribute('download', '教师课程统计'+date.toLocaleDateString()+'.xlsx');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }));
  }
}
