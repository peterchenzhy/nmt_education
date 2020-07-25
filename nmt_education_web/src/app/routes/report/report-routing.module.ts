import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DailySessionReportComponent } from './daily-session-report/daily-session-report.component';
import { FeeStatisticsReportComponent } from './fee-statistics-report/fee-statistics-report.component';

const routes: Routes = [
  { path: 'dailysession', component: DailySessionReportComponent },
  { path: 'feestatistics', component: FeeStatisticsReportComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule { }
