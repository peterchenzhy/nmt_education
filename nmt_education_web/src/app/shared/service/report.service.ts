import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { FeeStatisticsQueryParam } from 'src/app/model/system.model';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from '@delon/theme';

@Injectable()
export class ReportService {
    constructor(
        private settingService: SettingsService,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    public queryFeeStatistics(T: FeeStatisticsQueryParam): Observable<Object> {
        return this.httpClient.post('nmt-education/statistics/fee', T);
    }

}
