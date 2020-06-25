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
import { ResponseData, StudentQueryParam } from 'src/app/model/system.model';
import { StudentService } from '@shared/service/student.service';
import { StudentViewComponent } from '../student-view/student-view.component';

@Component({
    selector: 'app-student-list',
    templateUrl: './student-list.component.html',
})
export class StudentListComponent implements OnInit {
    loading = false;
    pager = {
        front: false
    };
    queryParam: StudentQueryParam = { pageNo: 1, pageSize: 10 };
    data: ResponseData = { list: [], total: 0 };
    genderList = this.globalService.GENDER_LIST;
    @ViewChild('st', { static: true })
    st: STComponent;
    columns: STColumn[] = [
        { title: '', index: 'id', type: 'checkbox' },
        { title: '学生编号', index: 'code' },
        { title: '姓名', index: 'name' },
        { title: '性别', index: 'sex', render: "genderRender" },
        { title: '联系电话', index: 'phone' },
        { title: '所在学校', index: 'school' },
        { title: '年级', index: 'grade',render:"gradeRender" },
        {
            title: '操作',
            buttons: [
                {
                    text: '编辑',
                    click: (item: any) => this.router.navigate([`/personnel/student/edit/${item.id}`, { student: JSON.stringify({ ...item, _values: undefined }) }])
                },
                {
                    text: '报名',
                    click: (item: Student) => this.router.navigate(['/order/create', { student: JSON.stringify({ ...item, _values: undefined }) }])

                },
            ],
        },
    ];
    selectedRows: STData[] = [];
    expandForm = false;

    constructor(
        public globalService: GlobalService,
        private studentService: StudentService,
        private router: Router,
        private http: _HttpClient,
        public msg: NzMessageService,
        private modalSrv: NzModalService,
        private cdr: ChangeDetectorRef,
    ) { }

    ngOnInit() {
        this.getData();
    }
    startQueryData() {
        this.queryParam.pageNo = 1;
        this.getData();
    }
    getData() {
        this.loading = true;
        this.studentService.queryStudents(this.queryParam)
            .pipe(
                tap(() => (this.loading = false)),
            )
            .subscribe((res: ResponseData) => {
                this.data = res;
                this.data.list = this.data.list == null ? [] : this.data.list;
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

    addNewStudent() {
        this.router.navigate([`/personnel/student/create`]);
    }

    reset() {
        // wait form reset updated finished
        this.queryParam.name = "";
        this.queryParam.phone = "";
        this.queryParam.pageNo = 1;
        setTimeout(() => this.getData());
    }
}
