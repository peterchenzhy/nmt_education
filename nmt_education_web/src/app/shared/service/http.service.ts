import { Injectable, Injector } from '@angular/core'
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class HttpService {
    constructor(
        private httpClient: HttpClient,
        private injector: Injector
    ) {
    }

    public loadSystemEnums(): Observable<Object> {
        return this.httpClient.get('nmt-education/enums/all?_allow_anonymous=true');
    }

  public getUsers(): Observable<Object> {
    return this.httpClient.get('nmt-education/user/getUsers?_allow_anonymous=true');
  }
}
