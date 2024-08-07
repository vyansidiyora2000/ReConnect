import "./shared/extensions/form.extension";

import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideAnimationsAsync } from "@angular/platform-browser/animations/async";
import { provideRouter, withHashLocation } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './shared/services/auth-interceptor.service';
import { JWTInterceptor } from "./shared/services/jwt-interceptor.service";
import { ToastService } from "./shared/services/toast.service";
import { MessageService } from "primeng/api";
import { DialogService, DynamicDialogConfig } from "primeng/dynamicdialog";

export const appConfig: ApplicationConfig = {
  providers: [provideAnimationsAsync(), provideRouter(routes, withHashLocation()), provideHttpClient(withInterceptors([authInterceptor, JWTInterceptor])), ToastService, MessageService, DialogService, DynamicDialogConfig]
};
