import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Country {
  countryId: number;
  countryName: string;
}
@Injectable({
  providedIn: 'root'
})
export class CountryService {

  constructor(private http: HttpClient) { }

  getAllCountries(): Observable<any> {
    return this.http.get<any>(`${environment.API}countries/getAllCountries`);
  }

  getCountryById(countryId: number): Observable<any> {
    return this.http.get<any>(`${environment.API}countries/getCountry/${countryId}`);
  }

  addCountry(country: Country): Observable<any> {
    return this.http.post<any>(`${environment.API}countries/addCountry`, country);
  }

  editCountry(country: Country): Observable<any> {
    return this.http.put<any>(`${environment.API}countries/editCountry`, country);
  }

  deleteCountry(countryId: number): Observable<any> {
    return this.http.delete<any>(`${environment.API}countries/deleteCountry/${countryId}`);
  }
}
