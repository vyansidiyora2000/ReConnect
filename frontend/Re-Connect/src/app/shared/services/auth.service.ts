
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { addMilliseconds, isBefore } from 'date-fns';
import { Router } from '@angular/router';

export interface LoginResponse {
    token: string,
    expiresIn: number,
    refreshToken: string,
    role: number,
    userEmail: string
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private _user: any;

    public get user(): any {
        return this._user;
    }
    public set user(value: any) {
        this._user = value;
    }

    constructor(private http: HttpClient, private router: Router) { }

    forgotPassword(email: string): Observable<any> {
        return this.http.post(environment.AUTH_API + 'forgotPassword', { email }, { responseType: 'text' });
    }

    resetPassword(token: string, newPassword: string): Observable<any> {
        return this.http.post(environment.AUTH_API + 'resetPassword', {
            token,
            newPassword
        }, { responseType: 'text' });
    }

    login({ email, password }) {
        return this.http.post<{ status: number, message: string, body: LoginResponse }>(environment.AUTH_API + 'login', { email, password }).pipe(
            tap({
                next: (response) => this.setSession(response), // Corrected to use response directly
                error: (error) => console.error('Login error:', error)
            })
        );
    }

    private setSession(authResult: any) {
        const expiresAt = addMilliseconds(new Date(), authResult.body.expiresIn);
        sessionStorage.setItem('token', authResult.body.token);
        sessionStorage.setItem("expiresIn", expiresAt.toISOString());
    }

    logout() {
        sessionStorage.removeItem("token");
        sessionStorage.removeItem("expiresIn");
        // this.toastService.showSuccess("User Logged out successfully");
        this.router.navigate(["/login"]);
    }

    public isLoggedIn() {
        const expiration = sessionStorage.getItem("expiresIn");
        const token = sessionStorage.getItem("token");
        if (!expiration || !token) {

            return false;
        }
        const expiresAt = new Date(expiration);
        return isBefore(new Date(), expiresAt);
    }

    isLoggedOut() {
        return !this.isLoggedIn();
    }

    getUserDetails(token: string) {
        return this.http.get(environment.AUTH_API + "getUserDetails?token=" + token).pipe(response => this.user = response);
    }
}
