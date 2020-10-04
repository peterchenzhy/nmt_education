import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap, map } from 'rxjs/operators';
import { STComponent, STColumn, STData, STChange } from '@delon/abc';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Student } from 'src/app/model/student.model';
import { Buffer } from 'buffer';
import { GlobalService } from '@shared/service/global.service';
import { ResponseData, StudentAccountQueryParam } from 'src/app/model/system.model';
import { StudentService } from '@shared/service/student.service';
import { AppContextService } from '@shared/service/appcontext.service';

@Component({
    selector: 'app-student-account',
    templateUrl: './student-account.component.html',
})
export class StudentAccountComponent implements OnInit {
    loading = false;
    pager = {
        front: false
    };
    queryParam: StudentAccountQueryParam = { pageNo: 1, pageSize: 10 };
    data: ResponseData = { list: [], total: 0 };
    genderList = this.globalService.GENDER_LIST;
    @ViewChild('st', { static: true })

    st: STComponent;
    columns: STColumn[] = [
        { title: '学生编号', index: 'studentCode' },
        { title: '姓名', index: 'studentName' },
        { title: '账户余额', index: 'amount' },
        { title: '备注', index: 'remark' },
        // {
        //     title: '操作',
        //     buttons: [
        //         {
        //             text: '编辑',
        //             click: (item: any) => this.router.navigate([`/personnel/student/edit/${item.id}`, { student: JSON.stringify({ ...item, _values: undefined }) }])
        //         },
        //         {
        //             text: '报名',
        //             click: (item: Student) => this.router.navigate(['/order/create', { student: JSON.stringify({ ...item, _values: undefined }) }])

        //         },
        //     ],
        // },
    ];
    selectedRows: STData[] = [];
    expandForm = false;

    constructor(
        public appCtx: AppContextService,
        public globalService: GlobalService,
        private studentService: StudentService,
        private router: Router,
        private http: _HttpClient,
        public msg: NzMessageService,
        private modalSrv: NzModalService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        //this.queryParam = this.appCtx.storageService.get("StudentAccountQuery") || this.queryParam;
        this.getData();
    }
    startQueryData() {
        this.queryParam.pageNo = 1;
        this.getData();
    }
    getData() {
        this.loading = true;
        this.studentService.queryStudentAccount(this.queryParam)
            .pipe(
                tap(() => { this.loading = false; }, () => { this.loading = false; })
            )
            .subscribe((res: ResponseData) => {
                this.data = res;
                this.data.list = this.data.list == null ? [] : this.data.list;
                this.cdr.detectChanges();
                //this.appCtx.storageService.set("StudentAccountQuery", this.queryParam);
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
        this.queryParam.studentId = null;
        this.queryParam.pageNo = 1;
        setTimeout(() => this.getData());
    }

    studentsOfOption: Array<Student> = [];
    nzFilterOption = () => true;
    searchStudent(value: string): void {
        if (!value || value == "") {
            return;
        }
        this.appCtx.studentService.fuzzyQueryStudents(value)
            .subscribe((data: Student[]) => {
                this.studentsOfOption = data;
            });
    }
}
