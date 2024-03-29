import { Injectable, Injector, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { zip } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MenuService, SettingsService, TitleService, ALAIN_I18N_TOKEN } from '@delon/theme';
import { DA_SERVICE_TOKEN, ITokenService } from '@delon/auth';
import { ACLService } from '@delon/acl';

import { NzIconService } from 'ng-zorro-antd/icon';
import { ICONS_AUTO } from '../../../style-icons-auto';
import { ICONS } from '../../../style-icons';
import { GlobalService } from '@shared/service/global.service';

/**
 * Used for application startup
 * Generally used to get the basic data of the application, like: Menu Data, User Data, etc.
 */
@Injectable()
export class StartupService {
  constructor(
    iconSrv: NzIconService,
    private menuService: MenuService,
    private settingService: SettingsService,
    private aclService: ACLService,
    private titleService: TitleService,
    @Inject(DA_SERVICE_TOKEN) private tokenService: ITokenService,
    private httpClient: HttpClient,
    public globalService: GlobalService,
    private injector: Injector
  ) {
    iconSrv.addIcon(...ICONS_AUTO, ...ICONS);
  }

  private viaHttp(resolve: any, reject: any) {
    zip(
      this.httpClient.get('assets/tmp/app-data.json')
    ).pipe(
      catchError(([appData]) => {
        resolve(null);
        return [appData];
      })
    ).subscribe(([appData]) => {

      // Application data
      const res: any = appData;
      // Application information: including site name, description, year
      this.settingService.setApp(res.app);
      // User information: including name, avatar, email address
      this.settingService.setUser(res.user);
      // ACL: Set the permissions to full, https://ng-alain.com/acl/getting-started
      this.aclService.setFull(true);
      // Menu data, https://ng-alain.com/theme/menu
      this.menuService.add(res.menu);
      // Can be set page suffix title, https://ng-alain.com/theme/title
      this.titleService.suffix = res.app.name;
    },
      () => { },
      () => {
        resolve(null);
      });
  }

  private viaMock(resolve: any, reject: any) {
    const tokenData = this.tokenService.get();
    // if (!tokenData.token) {
    //   this.injector.get(Router).navigateByUrl('/passport/login');
    //   resolve({});
    //   return;
    // }
    // mock
    const app: any = {
      name: `ng-alain`,
      description: `Ng-zorro admin panel front-end framework`
    };
    const user: any = {
      name: tokenData.name,
      avatar: './assets/tmp/img/avatar.jpg',
      email: '',
      token: tokenData.token
    };
    // Application information: including site name, description, year
    this.settingService.setApp(app);
    // User information: including name, avatar, email address
    this.settingService.setUser(user);
    // ACL: Set the permissions to full, https://ng-alain.com/acl/getting-started
    this.aclService.setFull(true);
    // Menu data, https://ng-alain.com/theme/menu
    this.menuService.add([
      {
        text: 'Main',
        group: true,
        children: [
          {
            text: '人员管理',
            group: true,
            children: [
              {
                text: "学生列表",
                link: '/personnel/student/list',
                icon: { type: 'icon', value: 'appstore' }
              },
              {
                text: "学生账户",
                link: '/personnel/student/account',
                icon: { type: 'icon', value: 'appstore' }
              },
              {
                text: "教师列表",
                link: '/personnel/teacher/list',
                icon: { type: 'icon', value: 'appstore' }
              }]
          },
          {
            text: '课程管理',
            group: true,
            children: [
              {
                text: "课程列表",
                link: '/course/list',
                icon: { type: 'icon', value: 'appstore' }
              }]
          },
          {
            text: '订单管理',
            group: true,
            children: [
              {
                text: "订单列表",
                link: '/order/list',
                icon: { type: 'icon', value: 'appstore' }
              },
              {
                text: "费用信息",
                link: '/order/statistics',
                icon: { type: 'icon', value: 'appstore' }
              }
              ]
          },
          {
            text: '报表',
            group: true,
            children: [
              {
                text: "科目统计",
                link: '/report/dailysession',
                icon: { type: 'icon', value: 'appstore' }
              },
              {
                text: "费用统计",
                link: '/report/feestatistics',
                icon: { type: 'icon', value: 'appstore' }
              }]
          }
        ]
      }
    ]);
    // Can be set page suffix title, https://ng-alain.com/theme/title
    this.titleService.suffix = app.name;

    resolve({});
  }

  load(): Promise<any> {
    // only works with promises
    // https://github.com/angular/angular/issues/15088
    return new Promise((resolve, reject) => {
      // http
      // this.viaHttp(resolve, reject);
      // mock：请勿在生产环境中这么使用，viaMock 单纯只是为了模拟一些数据使脚手架一开始能正常运行
      this.viaMock(resolve, reject);
      const tokenData = this.tokenService.get();
      if (tokenData.token) {
        this.globalService.loadSystemEnums();
      }
    });
  }
}
