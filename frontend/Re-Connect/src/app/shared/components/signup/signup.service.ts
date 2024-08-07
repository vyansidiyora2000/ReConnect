import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";

@Injectable({
    providedIn: "root"
})
export class SignUpService {
    constructor(private http: HttpClient) { }

    verifyEmail(body: { userType: number, email: string, password: string, reenteredPassword: string }) {
        return this.http.post(environment.AUTH_API + "verify-email", body);
    }

    signUp(body: FormData) {
        const headers = new HttpHeaders();
        return this.http.post(environment.AUTH_API + "signup", body, { headers });
    }
}