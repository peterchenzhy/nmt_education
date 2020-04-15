import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { SysEnums, SysEnum, StudentQueryParam } from 'src/app/model/system.model';
import { Student } from 'src/app/model/student.model';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from '@delon/theme';

@Injectable()
export class StudentService {
    constructor(
        private settingService: SettingsService,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    public queryStudents(T: StudentQueryParam): Observable<Object> {

        return this.httpClient.post('nmt-education/student/search', T);
    }

    public saveStudent(T: Student): Observable<Object> {

        return this.httpClient.post('nmt-education/student/manager', T);
    }
}