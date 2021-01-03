import { NgModule } from '@angular/core';
import { SharedModule } from '@shared';
import { OrderRoutingModule } from './order-routing.module';

import { OrderListComponent } from './order-list/order-list.component';
import { OrderViewComponent } from './order-view/order-view.component';
import { OrderRefundComponent } from './order-refund/order-refund.component';
import {OrderStatisticsComponent} from "./order-statistics/order-statistics.component";

const COMPONENTS = [
    OrderListComponent, OrderViewComponent, OrderRefundComponent,OrderStatisticsComponent
];
const COMPONENTS_NOROUNT = [
];

@NgModule({
    imports: [
        SharedModule,
        OrderRoutingModule
    ],
    declarations: [
        ...COMPONENTS,
        ...COMPONENTS_NOROUNT
    ],
    entryComponents: COMPONENTS_NOROUNT
})
export class OrderModule { }
