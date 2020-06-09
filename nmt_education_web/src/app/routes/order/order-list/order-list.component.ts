import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap, map } from 'rxjs/operators';
import { STComponent, STColumn, STData, STChange } from '@delon/abc';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from 'src/app/model/course.model';
import { Order } from 'src/app/model/order.model';
import { GlobalService } from '@shared/service/global.service';
import {CourseQueryParam, RegisterQueryParam, RegisterSummaryQueryParam, ResponseData} from "../../../model/system.model";
import {AppContextService} from "@shared/service/appcontext.service";

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
  pager = {
    front: false
  };
  queryParam: RegisterQueryParam = { pageNo: 1, pageSize: 10 };
  courseTypeList = this.appCtx.globalService.COURSE_TYPE_LIST;
  courseSubjectList = this.appCtx.globalService.COURSE_SUBJECT_LIST;
  courseClassificationList = this.appCtx.globalService.COURSE_CLASSIFICATION_LIST;
  courseStatusList = this.appCtx.globalService.COURSE_STATUS_LIST;
  feeTypeList = this.appCtx.globalService.FEE_TYPE_LIST;
  gradeList = this.appCtx.globalService.GRADE_LIST;
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
        { title: '订单编号', index: 'registrationNumber'},
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
            index: 'status',
            render: 'orderStatus'
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
        private appCtx: AppContextService,
        private globalService: GlobalService,
        private router: Router,
        public msg: NzMessageService,
        private modalSrv: NzModalService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.getData();
    }

    getData() {
        this.loading = true;
        this.appCtx.courseService.registerSearch(this.queryParam)
            .pipe(
                tap(() => (this.loading = false)),
            )
            .subscribe((res: ResponseData) => {
                res.list = res.list || [];
                // res.list.forEach(element => {
                //   element.statusDetail = this.appCtx.globalService.COURSE_STATUS_LIST[element.courseStatus];
                // });
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

    reset() {
        // wait form reset updated finished
        setTimeout(() => this.getData());
    }
}
