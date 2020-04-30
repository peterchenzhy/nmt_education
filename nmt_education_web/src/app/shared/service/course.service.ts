import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { TeacherQueryParam } from 'src/app/model/system.model';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from '@delon/theme';
import { Teacher } from 'src/app/model/teacher.model';
import { Course } from 'src/app/model/course.model';

@Injectable()
export class CourseService {
    constructor(
        private settingService: SettingsService,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    public queryCourses(T: TeacherQueryParam): Observable<Object> {
        return this.httpClient.post('nmt-education/course/search', T);
    }

    public saveCourse(T: Course): Observable<Object> {
        return this.httpClient.post('nmt-education/course/manager', T);
    }
}