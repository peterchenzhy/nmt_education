import { NgModule } from '@angular/core';
import { HttpService } from './http.service';
import { GlobalService } from './global.service';
import { CommonModule } from '@angular/common';
import { StudentService } from './student.service';

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
        GlobalService,
        StudentService,
        HttpService],
    entryComponents: COMPONENTS_NOROUNT
})
export class ServiceModule { }