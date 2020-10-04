import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { CourseQueryParam, RegisterQueryParam, RegisterSummaryQueryParam, TeacherQueryParam } from 'src/app/model/system.model';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from '@delon/theme';
import { Inject } from '@angular/core';
import { DA_SERVICE_TOKEN, ITokenService } from '@delon/auth';

@Injectable()
export class EntitlementService {
    constructor(
        @Inject(DA_SERVICE_TOKEN) private tokenService: ITokenService,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    canEditTeacherSalary(): boolean {
        let user = this.tokenService.get();
        let roleId = user && user.roleId ? user.roleId : "";
        return roleId == "91";
    }

}
