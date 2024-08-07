import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CardModule } from "primeng/card";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { PasswordModule } from "primeng/password";
import { DividerModule } from "primeng/divider";
import { StepperModule } from "primeng/stepper";
import { DropdownModule } from "primeng/dropdown";
import { MultiSelectModule } from "primeng/multiselect";
import { RadioButtonModule } from "primeng/radiobutton";
import { FileUploadModule } from 'primeng/fileupload';

import { SignUpService } from './signup.service';
import { Company, CompanyService } from '../../services/company.service';
import { Experience, ExperienceService } from '../../services/experience.service';
import { Skill, SkillsService } from '../../services/skills.service';
import { Country, CountryService } from '../../services/country.service';
import { City, CityService } from '../../services/city.service';
import { ROLES } from '../constants/roles';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { switchMap } from 'rxjs';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'rc-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CardModule, ButtonModule, InputTextModule, PasswordModule, DividerModule, ReactiveFormsModule, StepperModule, DropdownModule, MultiSelectModule, RadioButtonModule, FileUploadModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  userCredentialsForm: FormGroup;
  userDetailsForm: FormGroup;
  uploadForm: FormGroup;
  loading: boolean = false;
  activeStep: number = 0;
  roles = [{ roleId: 0, role: "Admin" }, { roleId: 1, role: "Referent" }, { roleId: 2, role: "Referrer" }];
  selectedRole = 1;
  verificationPending: boolean = false;
  isCreatingUser: boolean = false;

  listOfExperiences: Experience[] = [
    {
      "experienceId": 1,
      "experienceName": "Entry Level"
    },
    {
      "experienceId": 2,
      "experienceName": "Mid Level"
    },
    {
      "experienceId": 3,
      "experienceName": "Senior Level"
    },
    {
      "experienceId": 4,
      "experienceName": "Executive Level"
    }
  ]
  listOfCompanies: Company[] = [];
  listOfCountries: Country[] = [];
  listOfCities: City[] = [];
  listOfSkills: Skill[] = [];
  previewImg: any;

  constructor(private router: Router, private toastService: ToastService, private signUpService: SignUpService, private authService: AuthService, private companyService: CompanyService, private experienceService: ExperienceService, private skillsService: SkillsService, private countryService: CountryService, private cityService: CityService) {
    this.userCredentialsForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.maxLength(50)]),
      email: new FormControl('', [Validators.required, Validators.email, Validators.maxLength(50)]),
      password: new FormControl('', [Validators.required, Validators.minLength(5)]),
      confirmPassword: new FormControl('', [Validators.required, Validators.minLength(5)])
    });

    this.userDetailsForm = new FormGroup({
      company: new FormControl(null, [Validators.required]),
      experience: new FormControl(null, [Validators.required]),
      skills: new FormControl([], [Validators.required, Validators.minLength(1), Validators.maxLength(5)]),
      country: new FormControl(null, [Validators.required]),
      city: new FormControl({ value: null, disabled: true }, [Validators.required]),
    });

    this.uploadForm = new FormGroup({
      profileImage: new FormControl(null, [Validators.required]),
      profile: new FormControl(null, [Validators.required])
    });

    this.countryService.getAllCountries().subscribe((response: any) => {
      this.listOfCountries = response['body'];
    });
    this.companyService.getAllCompanies().subscribe((response: Company[]) => {
      this.listOfCompanies = response['body'];
    });
    // this.experienceService.getExperiences().subscribe((response: Experience[]) => {
    //   this.listOfExperiences = response['body'];
    // });
    this.skillsService.getAllSkills().subscribe((response: Skill[]) => {
      this.listOfSkills = response['body'];
    });

    this.userDetailsForm.controls['country'].valueChanges.pipe(
      switchMap(countryId => this.cityService.getAllCities(countryId))
    ).subscribe((response: City[]) => {
      this.listOfCities = response['body'];
      this.userDetailsForm.get('city')?.enable();
    })
  }

  onCheckForm(form: FormGroup, step: number) {
    form.markAllAsDirty();
    form.markAllAsTouched();

    if (!this.checkPasswordsMatch()) {
      this.toastService.showError("Passwords do not match!")
      console.error("Passwords do not match!");
      return;
    }

    if (form.invalid) {
      this.toastService.showFormError();
    } else {
      this.verificationPending = true;
      const body = {
        userType: this.selectedRole,
        name: form.get('name')?.value,
        email: form.get('email')?.value,
        password: form.get('password')?.value,
        reenteredPassword: form.get('confirmPassword')?.value
      }
      this.signUpService.verifyEmail(body).subscribe(response => {
        this.verificationPending = false;
        this.activeStep = 1;
      }, (error) => {
        this.verificationPending = false;
        this.toastService.showError("Email is already registered with us!");
      }, () => {
        this.verificationPending = false;
      })
    }
  }

  onSubmit() {
    this.userCredentialsForm.markAllAsDirty();
    this.userCredentialsForm.markAllAsTouched();
    this.userDetailsForm.markAllAsTouched();
    this.userDetailsForm.markAllAsDirty();
    if (this.userCredentialsForm.invalid || this.userDetailsForm.invalid) {
      this.toastService.showFormError();
    } else {
      this.isCreatingUser = true;
      const profileFile = this.uploadForm.controls['profile'].value;

      const formData = new FormData();

      formData.append('userType', this.selectedRole.toString());
      formData.append('userName', this.userCredentialsForm.controls['name'].value);
      formData.append('email', this.userCredentialsForm.controls['email'].value);
      formData.append('password', this.userCredentialsForm.controls['password'].value);
      formData.append('reenteredPassword', this.userCredentialsForm.controls['confirmPassword'].value);
      formData.append('company', this.userDetailsForm.controls['company'].value);
      formData.append('experience', this.userDetailsForm.controls['experience'].value);
      formData.append('skills', this.userDetailsForm.controls['skills'].value);
      formData.append('country', this.userDetailsForm.controls['country'].value);
      formData.append('city', this.userDetailsForm.controls['city'].value);
      formData.append('resume', "");  // Placeholder for resume field if needed
      formData.append('profile', profileFile, profileFile.name);

      this.signUpService.signUp(formData).subscribe(response => {
        this.isCreatingUser = false;
        this.toastService.showSuccess("User created successfully");
        this.router.navigate(["/", "login"]);
      }, (error) => {
        this.isCreatingUser = false;
      })
    }
  }

  private checkPasswordsMatch(): boolean {
    return this.userCredentialsForm.controls['password'].value === this.userCredentialsForm.controls['confirmPassword'].value;
  }

  onUpload(event: any) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.uploadForm.patchValue({
        profile: file
      });

      const reader = new FileReader();

      reader.onload = (e: any) => {

        this.previewImg = e.target.result;
      };

      reader.readAsDataURL(file);
    }
  }
}
