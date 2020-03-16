import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentListComponent } from './student/student-list/student-list.component';
//import { CourseViewComponent } from './course-view/course-view.component';

const routes: Routes = [
    {
        path: 'student',
        children: [
            { path: 'list', component: StudentListComponent },
            // { path: 'advanced', component: ProProfileAdvancedComponent },
        ]
    },
    //{ path: 'view/:courseno', component: CourseViewComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class StudentRoutingModule { }
