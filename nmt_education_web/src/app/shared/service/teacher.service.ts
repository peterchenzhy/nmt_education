import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { TeacherQueryParam } from 'src/app/model/system.model';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from '@delon/theme';
import { Teacher } from 'src/app/model/teacher.model';

@Injectable()
export class TeacherService {
    constructor(
        private settingService: SettingsService,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    public queryTeachers(T: TeacherQueryParam): Observable<Object> {
        return this.httpClient.post('nmt-education/teacher/search', T);
    }

    public saveTeacher(T: Teacher): Observable<Object> {
        return this.httpClient.post('nmt-education/teacher/manager', T);
    }
}