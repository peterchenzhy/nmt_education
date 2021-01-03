import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrderListComponent } from './order-list/order-list.component';
import { OrderViewComponent } from './order-view/order-view.component';
import { OrderRefundComponent } from './order-refund/order-refund.component';
import {OrderStatisticsComponent} from "./order-statistics/order-statistics.component";

const routes: Routes = [
  { path: 'list', component: OrderListComponent },
  { path: 'create', component: OrderViewComponent },
  { path: 'view/:id', component: OrderViewComponent },
  { path: 'refund/:id', component: OrderRefundComponent },
  { path: 'statistics', component: OrderStatisticsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderRoutingModule { }
