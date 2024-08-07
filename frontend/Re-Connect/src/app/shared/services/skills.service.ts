import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import {Observable} from "rxjs";

export interface Skill {
  skillId: number,
  skillName: String
}

@Injectable({
  providedIn: 'root'
})
export class SkillsService {
  private baseUrl = environment.API + 'skills';

  constructor(private http: HttpClient) {}

  getAllSkills(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getAllSkills`);
  }

  addSkill(skill: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/addSkill`, skill);
  }

  editSkill(skill: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/editSkill`, skill);
  }

  deleteSkill(skillId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteSkill/${skillId}`);
  }
}
