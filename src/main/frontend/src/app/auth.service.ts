import { HttpClient, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, shareReplay } from "rxjs";
import User from "./user";

@Injectable()
export class AuthService {
    constructor(private http: HttpClient) {
    }

    logIn(user: User): Observable<HttpResponse<Object>> {
        return this.http.post('http://localhost:8080/api/login', user, { observe: 'response' });
    }

    signUp(user: User): Observable<HttpResponse<Object>> {
        return this.http.post('http://localhost:8080/api/users', user, { observe: 'response' })
    }

    logOut() {
        localStorage.removeItem("token");
    }

    storeToken(token: string) {
        localStorage.setItem("token", token);
    }

    getToken(): string{
        return localStorage.getItem("token");
    }
}