import { Component, OnInit } from '@angular/core';
import { NzModalRef } from 'ng-zorro-antd/modal';
import { NzMessageService } from 'ng-zorro-antd/message';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
})
export class CourseViewComponent implements OnInit {
  course: any = {};
  i: any;

  constructor(
    private routerinfo: ActivatedRoute,
    public msgSrv: NzMessageService,
    public http: _HttpClient
  ) {
    this.course.courseNo = this.routerinfo.snapshot.params['courseno'];
  }

  ngOnInit(): void {
    this.http.get(`/course/${this.course.courseNo}`).subscribe(res => this.i = res);
  }

}
