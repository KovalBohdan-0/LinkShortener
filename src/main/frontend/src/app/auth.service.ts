import { HttpClient, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, shareReplay } from "rxjs";
import { environment } from "src/environments/environment.development";
import User from "./user";

@Injectable()
export class AuthService {
    endpoint = environment.domain + "api";


    constructor(private http: HttpClient) {
    }

    logIn(user: User): Observable<HttpResponse<Object>> {
        return this.http.post(this.endpoint + '/login', user, { observe: 'response' });
    }

    signUp(user: User): Observable<HttpResponse<Object>> {
        return this.http.post(this.endpoint + '/users', user, { observe: 'response' })
    }

    logOut() {
        localStorage.removeItem("token");
    }

    storeToken(token: string) {
        localStorage.setItem("token", token);
    }

    getToken(): string {
        return localStorage.getItem("token");
    }
}