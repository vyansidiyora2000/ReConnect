import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const UserResolver: ResolveFn<any> = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const authService = inject(AuthService);
    const token = sessionStorage.getItem("token");
    return authService.getUserDetails(token!);

}