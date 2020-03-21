import { Course } from './course.model';
import { Student } from './student.model';

export interface Order {
    id?: string;
    status?: number;
    course?: Course;
    student?: Student;
}