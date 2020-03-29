import { HttpRequest } from '@angular/common/http';
import { MockRequest } from '@delon/mock';
import { Order } from 'src/app/model/order.model';

const list: Order[] = [];

for (let i = 0; i < 46; i += 1) {
    list.push({
        id: `Order ${i}`,
        status: Math.floor(Math.random() * 10) % 5,
        signInDate: new Date(`2017-07-${i < 18 ? '0' + (Math.floor(i / 2) + 1) : Math.floor(i / 2) + 1}`),
        course: {
            code: `CourseCode ${i}`,
            name: '课程名',
            year: 2020,
            season: Math.floor(Math.random() * 10) % 4,
            grade: Math.floor(Math.random() * 10) % 8,
            subject: Math.floor(Math.random() * 10) % 5,
            type: Math.floor(Math.random() * 10) % 4,
            campus: (Math.floor(Math.random() * 10) % 4) + 1,
            status: Math.floor(Math.random() * 10) % 3,
            startDate: new Date(`2017-07-${i < 18 ? '0' + (Math.floor(i / 2) + 1) : Math.floor(i / 2) + 1}`)
        },
        student: {
            code: `StudentCode ${i}`,
            name: '学生名',
            contactNo: Math.floor(Math.random() * 1000).toString(),
            gender: Math.floor(Math.random() * 10) % 2
        },
        payList: [],
        sessionList: [],
        totalPrice: 1000,
        balance: 300
    });
}

function getOrder(params: any) {
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

function getOrderById(params: any) {
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

function removeOrder(nos: string): boolean {
    nos.split(',').forEach(no => {
    });
    return true;
}

function saveOrder(description: string) {
    const i = Math.ceil(Math.random() * 10000);
    list.unshift({

    });
}

export const ORDERS = {
    '/order': (req: MockRequest) => getOrder(req.queryString),
    '/order/:id': (req: MockRequest) => getOrderById(req.queryString),
    'DELETE /order': (req: MockRequest) => removeOrder(req.queryString.nos),
    'POST /order': (req: MockRequest) => saveOrder(req.body.description),
};
