import { Component, OnDestroy, OnInit } from '@angular/core';
import { HomepageService } from './homepage.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { TabViewModule } from 'primeng/tabview';
import { DataViewModule } from 'primeng/dataview';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { TagModule } from 'primeng/tag';
import { OverlayService } from '../../services/overlay.service';
import { ToastService } from '../../services/toast.service';
import { DialogService, DynamicDialogModule, DynamicDialogRef } from "primeng/dynamicdialog";
import { ProfileComponent } from '../profile/profile.component';
import { environment } from '../../../../environments/environment';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'rc-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, DropdownModule, InputTextModule, ButtonModule, TabViewModule, DataViewModule, TagModule, DynamicDialogModule, SkeletonModule],
})
export class HomepageComponent implements OnInit, OnDestroy {
  ref: DynamicDialogRef | undefined;
  searchQuery: string = '';
  selectedSearchType: any = { name: 'User', value: 'user' };
  searchTypes: any[] = [
    { name: 'User', value: 'user' },
    { name: 'Company', value: 'company' }
  ];
  searchResults: {
    profilePicture: string;
    name: string;
    companyName: string;
    experience: {
      years: number;
      level: string;
    };
    status: string | null;
  }[] = [];
  hasSearched: boolean = false;
  user: any;
  searching: boolean = false;
  userCategory: boolean = true;
  isSendingRequest: boolean = false;
  imagePath: string = environment.SERVER;

  constructor(private activatedRoute: ActivatedRoute, private searchService: HomepageService, private overlayService: OverlayService, private router: Router, private toastService: ToastService, public dialogService: DialogService) {
    //this.activatedRoute.data.subscribe(({ user }) => { this.user = user });
    this.activatedRoute.parent?.data.subscribe(({ user }) => {
      this.user = user;
    })
  }

  ngOnInit(): void {
    if (this.user.role == 2) {
      this.userCategory = false;
    }
  }

  show(userId: number) {
    this.ref = this.dialogService.open(ProfileComponent, {
      width: '50vw',
      contentStyle: { overflow: 'auto' }, header: 'Profile', data: { enableEdit: false, userId }
    });
  }

  getStatusSeverity(status: string): "success" | "danger" | "info" | "warning" {
    switch (status?.toLowerCase()) {
      case 'accepted':
        return "success";
      case 'rejected':
        return "danger";
      case 'pending':
        return "info";
      default:
        return "warning";
    }
  }

  getExperienceLevel(experience: number): string {
    if (experience <= 1) {
      return "Entry Level";
    } else if (experience <= 3) {
      return "Mid Level";
    } else if (experience <= 4) {
      return "Senior Level";
    } else {
      return "Executive Level";
    }
  }

  onSearch() {
    this.hasSearched = true;
    this.searching = true;
    this.overlayService.showOverlay("Searching users");
    if (this.selectedSearchType.value === 'user') {
      this.searchService.searchUsers(this.searchQuery)
        .pipe(finalize(() => { this.searching = false; this.overlayService.hideOverlay(); }))
        .subscribe(
          (response) => {
            this.processSearchResults(response);
          },
          (error) => {
            this.searchResults = [];
          }
        );
    } else {
      this.searchService.searchCompanyUsers(this.searchQuery)

        .pipe(finalize(() => { this.searching = false; this.overlayService.hideOverlay(); }))
        .subscribe(
          (response) => {
            this.processSearchResults(response);
          },
          (error) => {
            this.searchResults = [];
          }
        );
    }
  }
  private processSearchResults(response: any) {
    if (response && response.data && Array.isArray(response.data)) {
      this.searchResults = response.data.map((user: any) => ({
        profilePicture: user.profilePicture,
        name: user.userName,
        userId: user.userId,
        companyName: user.companyName,
        experience: {
          years: user.experience,
          level: this.getExperienceLevel(user.experience)
        },
        status: user.status
      }));
    } else {
      console.error('Unexpected response format', response);
      this.searchResults = [];
    }
  }

  redirectToProfile(userId: number): void {
    this.router.navigate(['other-profile', userId], { state: { editUser: false } });
  }

  sendRequest(user: any): void {
    this.isSendingRequest = true;
    this.searchService.sendRequest(user.userId).pipe(finalize(() => this.isSendingRequest = false)).subscribe(
      response => {
        this.toastService.showSuccess('Request sent successfully!');
        user.status = "PENDING";
      },
      error => {
        this.toastService.showError('Failed to send request.');
        console.error('Error sending request', error);
      }
    );
  }

  ngOnDestroy(): void {
    if (this.ref) {
      this.ref.close();
    }
  }
}