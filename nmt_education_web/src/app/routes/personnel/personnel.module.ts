import { NgModule } from '@angular/core';
import { SharedModule } from '@shared';
import { StudentRoutingModule } from './personnel-routing.module';
import { StudentAccountComponent } from './student/student-account/student-account.component';
import { StudentListComponent } from './student/student-list/student-list.component';
import { StudentViewComponent } from './student/student-view/student-view.component';
import { TeacherListComponent } from './teacher/teacher-list/teacher-list.component';
import { TeacherViewComponent } from './teacher/teacher-view/teacher-view.component';

const COMPONENTS = [
  StudentListComponent,
  StudentViewComponent,
  StudentAccountComponent,
  TeacherListComponent,
  TeacherViewComponent];
const COMPONENTS_NOROUNT = [
];

@NgModule({
  imports: [
    SharedModule,
    StudentRoutingModule
  ],
  declarations: [
    ...COMPONENTS,
    ...COMPONENTS_NOROUNT
  ],
  entryComponents: COMPONENTS_NOROUNT
})
export class PersonnelModule { }
