import { NgModule } from '@angular/core';
import { SharedModule } from '@shared';
import { StudentRoutingModule } from './personnel-routing.module';
import { StudentListComponent } from './student/student-list/student-list.component';
import { StudentViewComponent } from './student/student-view/student-view.component';


const COMPONENTS = [
  StudentListComponent,
  StudentViewComponent];
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
