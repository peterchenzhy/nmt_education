import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { Buffer } from 'buffer';
import { Order } from 'src/app/model/order.model';

@Component({
    selector: 'app-order-view',
    templateUrl: './order-view.component.html',
})
export class OrderViewComponent implements OnInit {

    order: Order = {};

    constructor(
        private fb: FormBuilder,
        private activaterRouter: ActivatedRoute,
        public msgSrv: NzMessageService,
        public http: _HttpClient
    ) {
    }
    ngOnInit(): void {
        let studentStr = this.activaterRouter.snapshot.params.student;
        if (studentStr) {
            this.order.student = JSON.parse(new Buffer(studentStr, 'base64').toString());
        }
        let courseStr = this.activaterRouter.snapshot.params.course;
        if (courseStr) {
            this.order.course = JSON.parse(new Buffer(courseStr, 'base64').toString());
        }
        
    }
}