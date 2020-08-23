import { Injectable } from '@angular/core';

@Injectable()
export class StorageService {

	constructor() { }
	public set(key: string, value: any): void {
		sessionStorage.setItem(key, JSON.stringify(value));
	}
	get(key: string): any {
		return JSON.parse(sessionStorage.getItem(key));
	}
	remove(key: string) {
		sessionStorage.removeItem(key);
	}
}