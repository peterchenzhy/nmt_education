import { Component, OnInit, ViewChild, TemplateRef, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { tap, map } from 'rxjs/operators';
import { STComponent, STColumn, STData, STChange } from '@delon/abc';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { GlobalService } from '@shared/service/global.service';
import { TeacherQueryParam, ResponseData } from 'src/app/model/system.model';
import { TeacherService } from '@shared/service/teacher.service';

@Component({
    selector: 'app-teacher-list',
    templateUrl: './teacher-list.component.html',
})
export class TeacherListComponent implements OnInit {
    loading = false;
    pager = {
        front: false
    };
    queryParam: TeacherQueryParam = { pageNo: 1, pageSize: 10 };
    data: ResponseData = { list: [], total: 0 };
    courseSubjects = this.globalService.COURSE_SUBJECT_LIST;
    genderList = this.globalService.GENDER_LIST;
    gradeList = this.globalService.GRADE_LIST;
    @ViewChild('st', { static: true })
    st: STComponent;
    columns: STColumn[] = [
        { title: '', index: 'id', type: 'checkbox' },
        //{ title: '教师编号', index: 'code' },
        { title: '姓名', index: 'name' },
        { title: '性别', index: 'sex', render: "genderRender" },
        // { title: '联系电话', index: 'phone' },
        // { title: '学校', index: 'school' },
        { title: '简介', index: 'remark' },
        {
            title: '操作',
            buttons: [
                {
                    text: '编辑',
                    click: (item: any) => this.router.navigate([`/personnel/teacher/edit/${item.id}`, { teacher: JSON.stringify({ ...item, _values: undefined }) }])
                }
            ]
        }
    ];
    selectedRows: STData[] = [];
    description = '';
    totalCallNo = 0;
    expandForm = false;

    constructor(
        public globalService: GlobalService,
        private teacherService: TeacherService,
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
        this.teacherService.queryTeachers(this.queryParam)
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

    createTeacher() {
        this.router.navigate([`/personnel/teacher/create`]);
    }

    reset() {
        // wait form reset updated finished
        this.queryParam.name = "";
        this.queryParam.phone = "";
        this.queryParam.pageNo = 1;
        setTimeout(() => this.getData());
    }

    // add(tpl: TemplateRef<{}>) {
    //     this.modalSrv.create({
    //         nzTitle: '新建规则',
    //         nzContent: tpl,
    //         nzOnOk: () => {
    //             this.loading = true;
    //             this.http.post('/course', { description: this.description }).subscribe(() => this.getData());
    //         },
    //     });
    // }
}
