import { HttpRequest } from '@angular/common/http';
import { MockRequest } from '@delon/mock';
import { Course } from 'src/app/model/course.model';

const list: Course[] = [];

for (let i = 0; i < 46; i += 1) {
    list.push({
        code: `CourseCode ${i}`,
        name: '课程名',
        year: 2020,
        season: Math.floor(Math.random() * 10) % 4,
        grade: Math.floor(Math.random() * 10) % 8,
        subject: Math.floor(Math.random() * 10) % 5,
        type: Math.floor(Math.random() * 10) % 4,
        campus: (Math.floor(Math.random() * 10) % 2) + 1,
        status: Math.floor(Math.random() * 10) % 3,
        startDate: new Date(`2017-07-${i < 18 ? '0' + (Math.floor(i / 2) + 1) : Math.floor(i / 2) + 1}`)
    });
}

function getRule(params: any) {
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
        ret = ret.filter(data => params.statusList.indexOf(data.status) > -1);
    }
    if (params.no) {
        ret = ret.filter(data => data.code.indexOf(params.no) > -1);
    }
    return ret;
}

function getRuleById(params: any) {
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
        ret = ret.filter(data => params.statusList.indexOf(data.status) > -1);
    }
    if (params.no) {
        ret = ret.filter(data => data.code.indexOf(params.no) > -1);
    }
    return ret[0];
}

function removeRule(nos: string): boolean {
    nos.split(',').forEach(no => {
        const idx = list.findIndex(w => w.code === no);
        if (idx !== -1) list.splice(idx, 1);
    });
    return true;
}

function saveRule(description: string) {
    const i = Math.ceil(Math.random() * 10000);
    list.unshift({
        code: `CourseCode ${i}`,
        name: '课程名',
        year: 2020,
        season: Math.floor(Math.random() * 10) % 4,
        grade: Math.floor(Math.random() * 10) % 8,
        subject: Math.floor(Math.random() * 10) % 5,
        type: Math.floor(Math.random() * 10) % 4,
        campus: (Math.floor(Math.random() * 10) % 2) + 1,
        status: Math.floor(Math.random() * 10) % 3,
        startDate: new Date(`2017-07-${i < 18 ? '0' + (Math.floor(i / 2) + 1) : Math.floor(i / 2) + 1}`)
   
    });
}

export const COURSES = {
    '/course': (req: MockRequest) => getRule(req.queryString),
    '/course/:id': (req: MockRequest) => getRuleById(req.queryString),
    'DELETE /course': (req: MockRequest) => removeRule(req.queryString.nos),
    'POST /course': (req: MockRequest) => saveRule(req.body.description),
};
