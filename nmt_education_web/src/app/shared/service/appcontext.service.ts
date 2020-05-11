import { Injectable } from '@angular/core'
import { GlobalService } from './global.service';
import { StudentService } from './student.service';
import { TeacherService } from './teacher.service';
import { CourseService } from './course.service';

@Injectable()
export class AppContextService {
    constructor(
        public globalService: GlobalService,
        public studentService: StudentService,
        public teacherService: TeacherService,
        public courseService: CourseService
    ) {
    }
}