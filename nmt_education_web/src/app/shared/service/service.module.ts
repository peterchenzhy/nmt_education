import { NgModule } from '@angular/core';
import { HttpService } from './http.service';
import { GlobalService } from './global.service';
import { CommonModule } from '@angular/common';
import { StudentService } from './student.service';
import { TeacherService } from './teacher.service';
import { CourseService } from './course.service';
import { AppContextService } from './appcontext.service';

const COMPONENTS = [

];
const COMPONENTS_NOROUNT = [];

@NgModule({
    imports: [CommonModule],
    declarations: [
        ...COMPONENTS,
        ...COMPONENTS_NOROUNT
    ],
    providers: [
        AppContextService,
        GlobalService,
        StudentService,
        TeacherService,
        CourseService,
        HttpService],
    entryComponents: COMPONENTS_NOROUNT
})
export class ServiceModule { }