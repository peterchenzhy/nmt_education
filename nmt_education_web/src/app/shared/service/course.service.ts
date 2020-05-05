import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import {CourseQueryParam, RegisterQueryParam, RegisterSummaryQueryParam, TeacherQueryParam} from 'src/app/model/system.model';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from '@delon/theme';
import { Teacher } from 'src/app/model/teacher.model';
import { Course } from 'src/app/model/course.model';
import { Order } from 'src/app/model/order.model';

@Injectable()
export class CourseService {
    constructor(
        private settingService: SettingsService,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    public queryCourses(T: CourseQueryParam): Observable<Object> {
        return this.httpClient.post('nmt-education/course/search', T);
    }

    public fuzzyQueryCourses(query: String): Observable<Object> {
        return this.httpClient.get('nmt-education/course/search/fuzzy?name=' + query);
    }

    public saveCourse(T: Course): Observable<Object> {
        return this.httpClient.post('nmt-education/course/manager', T);
    }

    public getCourseDetails(id: number): Observable<Object> {
        return this.httpClient.post(`nmt-education/course/detail/${id}`, id);
    }

    public registerCourse(T: Order): Observable<Object> {
        return this.httpClient.post('nmt-education/course/register', T);
    }
  public registerSearch(T: RegisterQueryParam): Observable<Object> {
    return this.httpClient.post('nmt-education/course/register/search', T);
  }
  public registerSummary(T: RegisterSummaryQueryParam): Observable<Object> {
    return this.httpClient.post('nmt-education/course/register/summary', T);
  }
}
