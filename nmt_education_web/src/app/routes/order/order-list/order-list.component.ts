import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { tap} from 'rxjs/operators';
import { STComponent, STColumn, STData, STChange } from '@delon/abc';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from 'src/app/model/course.model';
import { GlobalService } from '@shared/service/global.service';
import { RegisterQueryParam, ResponseData } from "../../../model/system.model";
import { AppContextService } from "@shared/service/appcontext.service";
import { DatePipe } from "@angular/common";

@Component({
    selector: 'app-order-list',
    templateUrl: './order-list.component.html',
})
export class OrderListComponent implements OnInit {

    signInDate = new FormGroup({
        signInDate: new FormControl()
    });
    courseDate = new FormGroup({
        courseDate: new FormControl()
    });
    searchDate: Date[];
    pager = {
        front: false
    };
    year: Date;
    queryParam: RegisterQueryParam = { pageNo: 1, pageSize: 25 };
    courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
    courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;
    seasonList = this.appCtx.globalService.SEASON_LIST;
    q: any = {
        pi: 1,
        ps: 10,
        sorter: '',
        orderCode: '',
        courseCode: '',
        status: null,
        studentCode: '',
        signInDate: ''
    };
    data: ResponseData = { list: [], total: 0 };
    loading = false;
    orderStatusList = this.globalService.ORDER_STATUS_LIST;

    @ViewChild('st', { static: true })
    st: STComponent;
    columns: STColumn[] = [
        { title: '', index: 'key', type: 'checkbox' },
        { title: '订单编号', index: 'registrationNumber' },
        { title: '姓名', index: 'studentName' },
        { title: '课程名', index: 'courseName' },
        {
            title: '报名时间',
            index: 'registerTime',
            type: 'date',
            sort: {
                compare: (a: any, b: any) => a.updatedAt - b.updatedAt,
            },
        },
        {
            title: '状态',
            index: 'registrationStatus',
            render: 'registrationStatus'
        },
        { title: '总金额', index: 'totalAmount' },
        { title: '余额', index: 'balanceAmount' },
        {
            title: '操作',
            buttons: [
                {
                    text: '编辑',
                    click: (item: any) => this.router.navigate([`/order/view/${item.id}`])
                },
                {
                    text: '退费',
                    click: (item: any) => this.router.navigate([`/order/refund/${item.id}`])
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
        public appCtx: AppContextService,
        public globalService: GlobalService,
        private router: Router,
        public msg: NzMessageService,
        private modalSrv: NzModalService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.queryParam = this.appCtx.storageService.get("OrderQuery") || this.queryParam;
        this.getData();
    }

    getData() {
        this.loading = true;
        if(this.year != null){
          this.queryParam.year = new Date(this.year).getFullYear();
        }
        this.appCtx.courseService.registerSearch(this.queryParam)
            .pipe(
                tap(() => { this.loading = false; }, () => { this.loading = false; })
            )
            .subscribe((res: ResponseData) => {
                res.list = res.list || [];
                // res.list.forEach(element => {
                //   element.registrationStatusDetail = this.appCtx.globalService.ORDER_STATUS_LIST[element.registrationStatus];
                // });
                this.data = res;
                this.cdr.detectChanges();
                this.appCtx.storageService.set("OrderQuery", this.queryParam);
            });
    }

    stChange(e: STChange) {
        switch (e.type) {
            case 'checkbox':
                this.selectedRows = e.checkbox!;
                this.totalCallNo = this.selectedRows.reduce((total, cv) => total + cv.callNo, 0);
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
        this.queryParam.signInDateStart = null;
        this.queryParam.signInDateEnd = null;
        this.queryParam.pageNo = 1;
        this.queryParam.pageSize = 25;
        this.queryParam.orderCode = null;
        this.queryParam.year = null;
        this.queryParam.season = null ;
        setTimeout(() => this.getData());
    }


    studentsOfOption: Array<Course> = [];
    nzFilterOption = () => true;
    searchStudent(value: string): void {
        if (!value || value == "") {
            return;
        }
        this.appCtx.studentService.fuzzyQueryStudents(value)
            .subscribe((data: Course[]) => {
                this.studentsOfOption = data;
            });
    }

    onDateRangeChanged(result: Date): void {
        this.queryParam.signInDateStart = this.datePipe.transform(result[0], 'yyyy-MM-dd');
        this.queryParam.signInDateEnd = this.datePipe.transform(result[1], 'yyyy-MM-dd');
    }
}
