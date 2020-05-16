import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DailySessionReportComponent } from './daily-session-report/daily-session-report.component';

const routes: Routes = [
  { path: 'dailysession', component: DailySessionReportComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule { }
