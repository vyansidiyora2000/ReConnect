import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

export interface Company {
  companyId?: number;
  companyName: string;
}

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient) { }

  getAllCompanies() {
    return this.http.get<Company[]>(`${environment.API}companies/getAllCompanies`);
  }

  getCompanyById(companyId: number) {
    return this.http.get<Company>(`${environment.API}companies/getCompany/${companyId}`);
  }

  addCompany(company: Company) {
    return this.http.post(`${environment.API}companies/addCompany`, company);
  }

  editCompany(company: Company) {
    return this.http.put(`${environment.API}companies/editCompany`, company);
  }

  deleteCompany(companyId: number) {
    return this.http.delete(`${environment.API}companies/deleteCompany/${companyId}`);
  }
}
