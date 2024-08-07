import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface City {
  cityId?: number;
  cityName: string;
  countryId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CityService {

  private apiUrl: string = environment.API + 'cities'; // Adjust as per your backend API

  constructor(private http: HttpClient) { }

  getAllCities(countryId?: number): Observable<any> {
    const url = countryId ? `${this.apiUrl}/getAllCities?countryId=${countryId}` : `${this.apiUrl}/getAllCities`;
    return this.http.get<any>(url);
  }

  getCityById(cityId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/getCity/${cityId}`);
  }

  addCity(city: City): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/addCity`, city);
  }

  editCity(city: City): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/editCity`, city);
  }

  deleteCity(cityId: number | undefined): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/deleteCity/${cityId}`);
  }
}
