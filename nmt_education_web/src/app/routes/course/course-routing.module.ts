import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CourseListComponent } from './course-list/course-list.component';
import { CourseViewComponent } from './course-view/course-view.component';
import { CourseSessionSignComponent } from './course-session-sign/course-session-sign.component';

const routes: Routes = [
  { path: 'list', component: CourseListComponent },
  { path: 'view/:id', component: CourseViewComponent, data: { title: '课程信息' } },
  { path: 'edit/:id', component: CourseViewComponent, data: { title: '课程信息' } },
  { path: 'create', component: CourseViewComponent, data: { title: '课程信息' } },
  { path: 'signsession/:id', component: CourseSessionSignComponent, data: { title: '课程签到' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CourseRoutingModule { }
