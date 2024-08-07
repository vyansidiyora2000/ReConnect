import { HttpClient,HttpHeaders,HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';


export interface Request {
    name: string;
    userId: number;
    profile: string;
}

@Injectable({
  providedIn: 'root'
})
export class RequestService 
{
  constructor(private http: HttpClient) { }

  getPendingRequest() 
  {
    return this.http.get<Request[]>(`${environment.API}getPendingRequest`);
  }

  getAcceptedRequest() {
    return this.http.get<Request[]>(`${environment.API}getAcceptedConnections`);
  }
  
  updateRequest(userId: number,status:boolean) 
  {
    const body = {
      userId: userId, 
      status: status };
    return this.http.post<undefined>(`${environment.API}updateRequestStatus`,body);
  }

}

