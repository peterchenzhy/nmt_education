import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CourseListComponent } from './course-list/course-list.component';
import { CourseViewComponent } from './course-view/course-view.component';

const routes: Routes = [
  { path: 'list', component: CourseListComponent },
  { path: 'view/:courseno', component: CourseViewComponent, data: { title: '课程信息' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CourseRoutingModule { }
