import {Injectable, Injector} from '@angular/core'
import {Observable} from 'rxjs';
import {FeeStatisticsQueryParam} from 'src/app/model/system.model';
import {HttpClient} from '@angular/common/http';
import {SettingsService} from '@delon/theme';

@Injectable()
export class ReportService {
  constructor(
    private settingService: SettingsService,
    private httpClient: HttpClient,
    private injector: Injector
  ) {
  }

  public queryFeeStatistics(T: FeeStatisticsQueryParam,F: Boolean): Observable<Object> {
    return this.httpClient.post(`nmt-education/statistics/fee?isManager=${F.valueOf()}`, T);
  }

  public queryFeeSummary(T: FeeStatisticsQueryParam,F: Boolean): Observable<Object> {
    return this.httpClient.post(`nmt-education/statistics/fee/summary?isManager=${F.valueOf()}`, T);
  }

  public exportFeeStatistics(T: FeeStatisticsQueryParam): Observable<Blob> {
    return this.httpClient.post('nmt-education/export/feeStatistics', T, {responseType: 'blob'});
    // return this.httpClient.post('nmt-education/export/feeStatistics', T);
  }

  public exportScheduleTeacher(T: FeeStatisticsQueryParam): Observable<Blob> {
    return this.httpClient.post('nmt-education/export/schedule/teacher', T, {responseType: 'blob'});
    // return this.httpClient.post('nmt-education/export/feeStatistics', T);
  }
  public exportTeacherSalary(T: FeeStatisticsQueryParam): Observable<Blob> {
    return this.httpClient.post('nmt-education/export/schedule/teacher/salary', T, {responseType: 'blob'});
    // return this.httpClient.post('nmt-education/export/feeStatistics', T);
  }
}
