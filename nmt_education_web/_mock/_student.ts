import { HttpRequest } from '@angular/common/http';
import { MockRequest } from '@delon/mock';
import { Student } from 'src/app/model/student.model';

const list: Student[] = [];

for (let i = 0; i < 46; i += 1) {
    list.push({
        // key: i,
        // disabled: i % 6 === 0,
        // href: 'https://ant.design',
        // avatar: [
        //     'https://gw.alipayobjects.com/zos/rmsportal/eeHMaZBwmTvLdIwMfBpg.png',
        //     'https://gw.alipayobjects.com/zos/rmsportal/udxAbMEhpwthVVcjLXik.png',
        // ][i % 2],
        id: i,
        name: '学生名',
        phone: Math.floor(Math.random() * 1000).toString(),
        sex: Math.floor(Math.random() * 10) % 2
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
    if (params.no) {
        ret = ret.filter(data => data.id == params.no);
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
    if (params.no) {
        ret = ret.filter(data => data.id == params.no);
    }
    return ret[0];
}

function removeRule(nos: number): boolean {
    const idx = list.findIndex(w => w.id === nos);
    if (idx !== -1) list.splice(idx, 1);
    return true;
}

function saveRule(description: string) {
    const i = Math.ceil(Math.random() * 10000);
    list.unshift({
        id: i,
        name: '学生名',
        phone: Math.floor(Math.random() * 1000).toString(),
        sex: Math.floor(Math.random() * 10) % 2
    });
}

export const STUDENTS = {
    '/student': (req: MockRequest) => getRule(req.queryString),
    '/student/:id': (req: MockRequest) => getRuleById(req.queryString),
    'DELETE /student': (req: MockRequest) => removeRule(req.queryString.nos),
    'POST /student': (req: MockRequest) => saveRule(req.body.description),
};
