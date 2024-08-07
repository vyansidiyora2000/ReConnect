import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { Observable } from "rxjs";

export interface UserNameTypeIdDTO {
    userId: number;
    userName: string;
    typeId: number;
}

@Injectable()
export class AdminUsersService {
    constructor(private http: HttpClient) { }
    private apiUrl = environment.API;


    getAllUsersByTypeId(typeId: number): Observable<UserNameTypeIdDTO[]> {
        const params = new HttpParams().set('typeId', typeId);
        return this.http.get<UserNameTypeIdDTO[]>(this.apiUrl + `users/getAllUsersByTypeId`, { params });
    }

    deleteUser(userId: number): Observable<void> {
        return this.http.delete<void>(this.apiUrl + `users/${userId}`);
    }
}