import { City, Company, Country, Skill, UserDetails } from "./user-details.model";
import { ActivatedRoute } from "@angular/router";
import { ProfileService } from "./profile.service";
import { MessageService } from "primeng/api";
import { CompanyService } from "../../services/company.service";
import { SkillsService } from "../../services/skills.service";
import { CountryService } from "../../services/country.service";
import { CityService } from "../../services/city.service";
import { Experience } from "../../services/experience.service";
import { Button } from "primeng/button";
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgClass, NgForOf, NgIf } from "@angular/common";
import { CardModule } from "primeng/card";
import { FileUploadModule } from "primeng/fileupload";
import { DropdownModule } from "primeng/dropdown";
import { MultiSelectModule } from "primeng/multiselect";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { finalize, forkJoin, Observable, Subscription } from "rxjs";
import { ChipsModule } from "primeng/chips";
import { ChipModule } from "primeng/chip";
import { InputTextModule } from "primeng/inputtext";
import { OverlayService } from "../../services/overlay.service";
import { environment } from "../../../../environments/environment";
import { DynamicDialogConfig } from "primeng/dynamicdialog";
import { SkeletonModule } from "primeng/skeleton";

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.scss'],
    standalone: true,
    imports: [
        Button,
        FormsModule,
        NgIf,
        CardModule,
        FileUploadModule,
        DropdownModule,
        ReactiveFormsModule,
        NgForOf,
        MultiSelectModule,
        ChipsModule,
        ChipModule,
        InputTextModule,
        NgClass,
        SkeletonModule
    ],
    providers: []
})
export class ProfileComponent implements OnInit, OnDestroy {
    profileForm!: FormGroup;
    userDetails: UserDetails = {
        detailId: 0,
        userName: '',
        experience: 0,
        resume: '',
        profilePicture: '',
        city: 0,
        country: 0,
        company: 0,
        cityName: '',
        countryName: '',
        companyName: '',
        skills: []
    };

    serverPath: String = environment.SERVER;
    resumeUrl: any;
    showEditForm: boolean = false;
    enableEdit: boolean = true;
    userID: string = "0";
    profilePictureFile: File | null = null;
    resumeFile: File | null = null;
    activatedRoute$?: Subscription = new Subscription();

    listOfExperiences: Experience[] = [
        { "experienceId": 1, "experienceName": "Entry Level" },
        { "experienceId": 2, "experienceName": "Mid Level" },
        { "experienceId": 3, "experienceName": "Senior Level" },
        { "experienceId": 4, "experienceName": "Executive Level" }
    ];
    listOfCompanies: Company[] = [];
    listOfCountries: Country[] = [];
    listOfCities: City[] = [];
    listOfSkills: Skill[] = [];
    isFetchingUserDetails: boolean = false;

    constructor(
        private activatedRoute: ActivatedRoute,
        private profileService: ProfileService,
        private messageService: MessageService,
        private companyService: CompanyService,
        private skillsService: SkillsService,
        private countryService: CountryService,
        private cityService: CityService,
        private overlayService: OverlayService,
        public dialogConfig: DynamicDialogConfig
    ) {
    }

    ngOnInit(): void {
        if (this.dialogConfig.data) {
            this.userID = this.dialogConfig.data.userId;
            this.enableEdit = this.dialogConfig.data.enableEdit;
        } else {
            this.activatedRoute$ = this.activatedRoute.parent?.data.subscribe(({ user }) => {
                this.userID = user['userId'];
            });
        }

        this.profileForm = new FormGroup({
            userName: new FormControl(this.userDetails.userName, [Validators.required]),
            company: new FormControl(null, [Validators.required]),
            experience: new FormControl(null, [Validators.required]),
            skills: new FormControl([], [Validators.required]),
            country: new FormControl(null, [Validators.required]),
            city: new FormControl(null, [Validators.required]),
            profilePicture: new FormControl(null),
            resume: new FormControl(null)
        });

        this.fetchDropdownOptionsAndUserDetails();
    }


    onProfilePictureSelect(event: any) {
        this.profilePictureFile = event.files[0];
    }

    onResumeSelect(event: any) {
        this.resumeFile = event.files[0];
    }

    fetchDropdownOptionsAndUserDetails() {
        this.overlayService.showOverlay("Fetching user details");
        this.isFetchingUserDetails = true;
        forkJoin({
            companies: this.companyService.getAllCompanies(),
            countries: this.countryService.getAllCountries(),
            skills: this.skillsService.getAllSkills(),
            cities: this.cityService.getAllCities(),
            userDetails: this.profileService.getUserDetails(this.userID)
        }).pipe(finalize(() => { this.overlayService.hideOverlay(); this.isFetchingUserDetails = false; })).subscribe(({ companies, countries, skills, cities, userDetails }) => {
            this.listOfCompanies = companies['body'];
            this.listOfCountries = countries['body'];
            this.listOfSkills = skills['body'];
            this.listOfCities = cities['body'];
            this.userDetails = userDetails;

            const company = this.listOfCompanies.find(c => c.companyId === userDetails.company);
            const country = this.listOfCountries.find(c => c.countryId === userDetails.country);
            const city = this.listOfCities.find(c => c.cityId === userDetails.city);

            this.userDetails.companyName = company ? company.companyName : "";
            this.userDetails.countryName = country ? country.countryName : "";
            this.userDetails.cityName = city ? city.cityName : "";

            this.profileForm.patchValue({
                userName: userDetails.userName,
                company: company ? company : null,
                experience: userDetails.experience,
                profilePicture: userDetails.profilePicture,
                country: country ? country : null,
                city: city ? city : null,
                skills: userDetails.skills?.map(skill => skill.skillId) || []
            });


            if (userDetails.country) {
                this.loadCities(userDetails.country);
            }

            // Listen for country changes to load cities dynamically
            this.profileForm.get('country')?.valueChanges.subscribe(({ countryId }) => {
                this.loadCities(countryId);
            });
        });
    }

    loadCities(countryId: number) {
        this.cityService.getAllCities(countryId).subscribe((response: any[]) => {
            this.listOfCities = response['body'];
        });


    }

    updateUserDetails() {
        const formValues = this.profileForm.value;

        const updatedUserDetailsRequest = {
            userId: this.userID,
            userName: formValues.userName,
            experience: formValues.experience?.experienceId || formValues.experience,
            company: formValues.company?.companyId || formValues.company,
            city: formValues.city?.cityId || formValues.city,
            country: formValues.country?.countryId || formValues.country,
            skillIds: formValues.skills || []
        };

        const formData = new FormData();
        formData.append('userDetails', JSON.stringify(updatedUserDetailsRequest));
        if (this.profilePictureFile) {
            formData.append('profilePicture', this.profilePictureFile);
        }
        if (this.resumeFile) {
            formData.append('resume', this.resumeFile);
        }

        this.profileService.updateUserDetails(formData).subscribe((data: UserDetails) => {
            this.userDetails = data;

            const company = this.listOfCompanies.find(c => c.companyId === data.company);
            const country = this.listOfCountries.find(c => c.countryId === data.country);
            const city = this.listOfCities.find(c => c.cityId === data.city);

            this.userDetails.companyName = company ? company.companyName : "";
            this.userDetails.countryName = country ? country.countryName : "";
            this.userDetails.cityName = city ? city.cityName : "";
            this.showEditForm = false;
            this.messageService.add({
                severity: 'success',
                summary: 'Success',
                detail: 'User details updated successfully'
            });
        });
    }

    getResume() {
        this.profileService.getResume(this.userID).subscribe((blob: Blob) => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'resume.pdf';
            a.click();
            window.URL.revokeObjectURL(url);
        });
    }

    ngOnDestroy(): void {
        this.activatedRoute$?.unsubscribe();
    }

}
