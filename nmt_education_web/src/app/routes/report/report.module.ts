import { NgModule } from '@angular/core';
import { SharedModule } from '@shared';
import { ReportRoutingModule } from './report-routing.module';
import { DailySessionReportComponent } from './daily-session-report/daily-session-report.component';
import { FeeStatisticsReportComponent } from './fee-statistics-report/fee-statistics-report.component';

const COMPONENTS = [
  DailySessionReportComponent,
  FeeStatisticsReportComponent];
const COMPONENTS_NOROUNT = [
];

@NgModule({
  imports: [
    SharedModule,
    ReportRoutingModule
  ],
  declarations: [
    ...COMPONENTS,
    ...COMPONENTS_NOROUNT
  ],
  entryComponents: COMPONENTS_NOROUNT
})
export class ReportModule { }
