import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap, map } from 'rxjs/operators';
import { STComponent, STColumn, STData, STChange } from '@delon/abc';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { GENDER_LIST, getGenderLabel } from '@shared/constant/system.constant';

@Component({
    selector: 'app-student-list',
    templateUrl: './student-list.component.html',
})
export class StudentListComponent implements OnInit {
    getGenderLabel = getGenderLabel;
    q: any = {
        pi: 1,
        ps: 10,
        sorter: '',
        courseNo: '',
        status: null,
        name: '',
        startDate: '',
    };
    data: any[] = [];
    loading = false;
    genderList = GENDER_LIST;
    @ViewChild('st', { static: true })
    st: STComponent;
    columns: STColumn[] = [
        { title: '', index: 'key', type: 'checkbox' },
        { title: '学生编号', index: 'studentNo' },
        { title: '姓名', index: 'studentName' },
        { title: '性别', index: 'gender', render: "genderRender" },
        { title: '联系电话', index: 'contactNo' },
        {
            title: '操作',
            buttons: [
                {
                    text: '编辑',
                    click: (item: any) => this.router.navigate([`/personnel/student/view/${item.studentNo}`]),
                },
                {
                    text: '报名',
                    click: (item: any) => this.msg.success(`报名${item.studentNo}`),
                },
            ],
        },
    ];
    selectedRows: STData[] = [];
    description = '';
    totalCallNo = 0;
    expandForm = false;

    constructor(
        private router: Router,
        private http: _HttpClient,
        public msg: NzMessageService,
        private modalSrv: NzModalService,
        private cdr: ChangeDetectorRef,
    ) { }

    ngOnInit() {
        this.getData();
    }

    getData() {
        this.loading = true;
        //this.q.statusList = this.status.filter(w => w.checked).map(item => item.index);
        // if (this.q.status !== null && this.q.status > -1) {
        //     this.q.statusList.push(this.q.status);
        // }
        this.http
            .get('/student', this.q)
            .pipe(
                map((list: any[]) =>
                    list.map(i => {
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
