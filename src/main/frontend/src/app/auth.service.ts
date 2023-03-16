import { HttpClient, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, shareReplay } from "rxjs";
import User from "./user";

@Injectable()
export class AuthService {
    // domain = "http://linkshortener.eu-north-1.elasticbeanstalk.com/api";
    domain = "http://localhost:8080/api";


    constructor(private http: HttpClient) {
    }

    logIn(user: User): Observable<HttpResponse<Object>> {
        return this.http.post(this.domain + '/login', user, { observe: 'response' });
    }

    signUp(user: User): Observable<HttpResponse<Object>> {
        return this.http.post(this.domain + '/users', user, { observe: 'response' })
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