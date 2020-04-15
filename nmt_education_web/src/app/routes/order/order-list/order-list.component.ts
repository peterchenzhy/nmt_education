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

@Component({
    selector: 'app-order-list',
    templateUrl: './order-list.component.html',
})
export class OrderListComponent implements OnInit {

    signInDate = new FormGroup({
        signInDate: new FormControl()
    });
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
    data: Order[] = [];
    loading = false;
    orderStatusList = this.globalService.ORDER_STATUS_LIST;

    @ViewChild('st', { static: true })
    st: STComponent;
    columns: STColumn[] = [
        { title: '', index: 'key', type: 'checkbox' },
        { title: '订单编号', index: 'id' },
        { title: '姓名', index: 'student', render: "studentName" },
        { title: '课程名', index: 'course', render: "courseName" },
        {
            title: '报名时间',
            index: 'signInDate',
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
        { title: '总金额', index: 'totalPrice' },
        { title: '余额', index: 'balance' },
        {
            title: '操作',
            buttons: [
                {
                    text: '编辑',
                    click: (item: any) => this.router.navigate([`/order/view/${item.id}`]),
                }
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
            .get('/order', this.q)
            .pipe(
                map((list: Order[]) =>
                    list.map(i => {
                        i.statusDetail = this.globalService.ORDER_STATUS_LIST[i.status];
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

    add(tpl: TemplateRef<{}>) {
        this.modalSrv.create({
            nzTitle: '新建规则',
            nzContent: tpl,
            nzOnOk: () => {
                this.loading = true;
                this.http.post('/course', { description: this.description }).subscribe(() => this.getData());
            },
        });
    }

    reset() {
        // wait form reset updated finished
        setTimeout(() => this.getData());
    }
}
