import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";

@Injectable()
export class DashboardService {

    constructor(private http: HttpClient) { }

    getAllUsersPerCountry() {
        return this.http.get(environment.API + "dashboard/getAllUsersPerCountry");
    }

    getAllUsersPerType() {
        return this.http.get(environment.API + "dashboard/getAllUsersPerType");
    }

    getAllUsersPerCompany() {
        return this.http.get(environment.API + "dashboard/getAllUsersPerCompany");
    }

    getTopFiveCompanies() {
        return this.http.get(environment.API + "dashboard/getTopFiveCompanies");
    }
}