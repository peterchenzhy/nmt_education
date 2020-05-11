import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrderListComponent } from './order-list/order-list.component';
import { OrderViewComponent } from './order-view/order-view.component';

const routes: Routes = [
  { path: 'list', component: OrderListComponent },
  { path: 'create', component: OrderViewComponent },
  { path: 'view/:id', component: OrderViewComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderRoutingModule { }
