import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentListComponent } from './student/student-list/student-list.component';
import { StudentViewComponent } from './student/student-view/student-view.component';
import { TeacherListComponent } from './teacher/teacher-list/teacher-list.component';
import { TeacherViewComponent } from './teacher/teacher-view/teacher-view.component';

const routes: Routes = [
    {
        path: 'student',
        children: [
            { path: 'list', component: StudentListComponent },
            { path: 'view/:code', component: StudentViewComponent, data: { title: '学生信息' } }
        ]
    },
    {
        path: 'teacher',
        children: [
            { path: 'list', component: TeacherListComponent },
            { path: 'view/:code', component: TeacherViewComponent, data: { title: '教师信息' } }
        ]
    }
    //{ path: 'view/:courseno', component: CourseViewComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class StudentRoutingModule { }
