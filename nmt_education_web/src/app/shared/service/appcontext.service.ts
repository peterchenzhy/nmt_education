import { Injectable } from '@angular/core'
import { GlobalService } from './global.service';
import { StudentService } from './student.service';
import { TeacherService } from './teacher.service';
import { CourseService } from './course.service';
import { ReportService } from './report.service';
import { StorageService } from './storage.service';

@Injectable()
export class AppContextService {
    constructor(
        public globalService: GlobalService,
        public studentService: StudentService,
        public teacherService: TeacherService,
        public courseService: CourseService,
        public reportService: ReportService,
        public storageService: StorageService
    ) {
    }
}