import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDetails } from './user-details.model';
import {environment} from "../../../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private apiUrl: string = environment.API + 'profile';

    constructor(private http: HttpClient) { }

    getUserDetails(userID: string): Observable<UserDetails> {
        const params = new HttpParams().set('userId', userID);
        return this.http.get<UserDetails>(`${this.apiUrl}`, { params });
    }

    updateUserDetails(formData: FormData): Observable<UserDetails> {
        return this.http.post<UserDetails>(`${this.apiUrl}/updateUserDetails`, formData);
    }

    getResume(userID: string): Observable<Blob> {
        const params = new HttpParams().set('userId', userID);
        return this.http.get(`${this.apiUrl}/resume`, { params, responseType: 'blob' });
    }

    getProfilePicture(userID: string): Observable<Blob> {
        const params = new HttpParams().set('userId', userID);
        return this.http.get(`${this.apiUrl}/profilePicture`, { params, responseType: 'blob' });
    }
}
