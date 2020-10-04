import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { SysEnums, SysEnum, StudentQueryParam, StudentAccountQueryParam } from 'src/app/model/system.model';
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

    public fuzzyQueryStudents(query: String): Observable<Object> {
        return this.httpClient.get('nmt-education/student/search/fuzzy?name=' + query);
    }

    public getBalance(studentId: number): Observable<Object> {
        return this.httpClient.post(`nmt-education/student/account/${studentId}`, null);
    }

    public queryStudentAccount(T: StudentAccountQueryParam): Observable<Object> {
        let param = `pageNo=${T.pageNo}&pageSize=${T.pageSize}`;
        if (T.studentId) {
            param = `${param}&studentId=${T.studentId}`;
        }
        return this.httpClient.get(`nmt-education/student/accountPage?${param}`);
    }

    public saveBalanceAccount(T: any): Observable<Object> {
        return this.httpClient.post(`nmt-education/student/account/edit`, T);
    }
    
}
