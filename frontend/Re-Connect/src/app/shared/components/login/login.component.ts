import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { MessageService } from 'primeng/api';
import { RouterLink } from '@angular/router';
import { RadioButtonModule } from 'primeng/radiobutton';
import { Router } from '@angular/router';
import { AuthService, LoginResponse } from '../../services/auth.service';
import { finalize, tap } from 'rxjs';

@Component({
  selector: 'rc-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    CardModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    ReactiveFormsModule,
    RadioButtonModule,
    HttpClientModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loading: boolean = false;
  userCredentialsForm: FormGroup;
  activeStep: number = 0;
  roles = [{ roleId: 0, role: 'Referrer' }, { roleId: 1, role: 'Referent' }];
  selectedRole = 0;



  constructor(private messageService: MessageService, private authService: AuthService, private router: Router) {
    this.userCredentialsForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required]),
    });
  }

  onSubmit() {
    this.userCredentialsForm.markAllAsDirty();
    this.userCredentialsForm.markAllAsTouched();
    if (this.userCredentialsForm.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Error in form'
      });
    } else {
      this.loading = true;
      const body = {
        email: this.userCredentialsForm.controls['email'].value,
        password: this.userCredentialsForm.controls['password'].value
      };

      this.authService.login(body).pipe(finalize(() => this.loading = false)).subscribe(
        (response) => {
          // Handle successful login response
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Login successful'
          });
          if (response.body.role == 0) {
            this.router.navigate(["/", "admin"]);
          } else {
            this.router.navigate(['/', "homepage"]);
          }
        },
        (error: any) => {
          // Handle login error
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Email or password is incorrect'
          });
        }
      );
    }
  }
}
