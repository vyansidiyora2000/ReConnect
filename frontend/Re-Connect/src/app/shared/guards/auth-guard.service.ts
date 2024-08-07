import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router, CanActivateChildFn, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';



export const canActivatePage: CanActivateFn = (
  next: ActivatedRouteSnapshot,
  state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  if (!authService.isLoggedIn()) {
    return true; // User is not authenticated, allow access to route
  } else {
    return router.createUrlTree(["/", "homepage"])
  }
}

export const RoleGuard: CanActivateChildFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.user; // Get user details from resolver
  const roleId = user.roleId;

  if (roleId === 0) {
    router.createUrlTree(['/admin']); // Redirect to admin route if roleId is 0
  } else {
    router.createUrlTree(['/homepage']); // Redirect to homepage route for other roles
  }

  return true;
}

export const canActivateChildPage: CanActivateChildFn = (childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  if (authService.isLoggedIn()) {
    const roleId = authService.user['roleId'];

    return true; // User is authenticated, allow access to route
  } else {
    return router.createUrlTree(['/login']); // User is authenticated, redirect to login page
  }
};

export const canActivateHomePage: CanActivateChildFn = (childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {


  // const authService = inject(AuthService);
  // const router = inject(Router);
  // if (authService.isLoggedIn() && childRoute.parent?.data['user'].roleId > 0) {
  //   return true; // User is authenticated, allow access to route
  // } else if (authService.isLoggedIn() && childRoute.parent?.data['user'].roleId == 0) {
  //   return router.createUrlTree(['/admin']);
  // } else {
  //   return router.createUrlTree(['/login']); // User is authenticated, redirect to login page
  // }
  return true;
};

export const canActivateAdminPage: CanActivateChildFn = (childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  if (authService.isLoggedIn() && childRoute.parent?.data['user'].roleId == 0) {
    return true; // User is authenticated, allow access to route
  } else if (authService.isLoggedIn() && childRoute.parent?.data['user'].roleId > 0) {
    return router.createUrlTree(['/homepage']); // User is authenticated, allow access to route
  } else {
    return router.createUrlTree(['/login']); // User is authenticated, redirect to login page
  }
};
