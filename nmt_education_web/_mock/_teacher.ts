import { HttpRequest } from '@angular/common/http';
import { MockRequest } from '@delon/mock';
import { Teacher } from 'src/app/model/teacher.model';

const list: Teacher[] = [];

for (let i = 0; i < 46; i += 1) {
    list.push({
        code: `TeacherCode ${i}`,
        name: '教师名',
        phone: "130********",
        gender: Math.floor(Math.random() * 10) % 2,
        introduction: "教师介绍"
    });
}

function getTeacher(params: any) {
    let ret = [...list];
    if (params.sorter) {
        const s = params.sorter.split('_');
        ret = ret.sort((prev, next) => {
            if (s[1] === 'descend') {
                return next[s[0]] - prev[s[0]];
            }
            return prev[s[0]] - next[s[0]];
        });
    }
    if (params.statusList && params.statusList.length > 0) {
    }
    if (params.no) {
    }
    return ret;
}

function getTeacherById(params: any) {
    let ret = [...list];
    if (params.sorter) {
        const s = params.sorter.split('_');
        ret = ret.sort((prev, next) => {
            if (s[1] === 'descend') {
                return next[s[0]] - prev[s[0]];
            }
            return prev[s[0]] - next[s[0]];
        });
    }
    if (params.statusList && params.statusList.length > 0) {
    }
    if (params.no) {
    }
    return ret[0];
}

function removeTeacher(nos: string): boolean {
    nos.split(',').forEach(no => {
    });
    return true;
}

function saveTeacher(description: string) {
    const i = Math.ceil(Math.random() * 10000);
    list.unshift({

    });
}

export const TEACHERS = {
    '/teacher': (req: MockRequest) => getTeacher(req.queryString),
    '/teacher/:id': (req: MockRequest) => getTeacherById(req.queryString),
    'DELETE /teacher': (req: MockRequest) => removeTeacher(req.queryString.nos),
    'POST /teacher': (req: MockRequest) => saveTeacher(req.body.description),
};
