import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class SkillDomainService {
    private baseUrl = environment.API + 'skillDomains';

    constructor(private http: HttpClient) { }

    getAllSkillDomains(): Observable<any> {
        return this.http.get(`${this.baseUrl}/getAllSkillDomains`);
    }

    addSkillDomain(skillDomain: any): Observable<any> {
        return this.http.post(`${this.baseUrl}/addSkillDomain`, skillDomain);
    }

    editSkillDomain(skillDomain: any): Observable<any> {
        return this.http.put(`${this.baseUrl}/editSkillDomain`, skillDomain);
    }

    deleteSkillDomain(domainId: number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/deleteSkillDomain/${domainId}`);
    }
}
