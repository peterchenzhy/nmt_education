import { NgModule } from '@angular/core';
import { SharedModule } from '@shared';
import { OrderRoutingModule } from './order-routing.module';

import { OrderListComponent } from './order-list/order-list.component';
import { OrderViewComponent } from './order-view/order-view.component';

const COMPONENTS = [
    OrderListComponent, OrderViewComponent
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
