import { Component, OnDestroy, OnInit } from '@angular/core';
import { TabViewChangeEvent, TabViewModule } from 'primeng/tabview';
import { DataViewModule } from 'primeng/dataview';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { environment } from '../../../../environments/environment';
import { Request, RequestService } from '../requests/request.service';
import { ToastService } from '../../services/toast.service';
import { ToastModule } from "primeng/toast";
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ProfileComponent } from '../profile/profile.component';
import { finalize } from 'rxjs';
import { SkeletonModule } from 'primeng/skeleton';
import { BadgeModule } from 'primeng/badge';

@Component({
  selector: 'rc-requests',
  standalone: true,
  imports: [CommonModule, ButtonModule, TabViewModule, DataViewModule, ToastModule, SkeletonModule, BadgeModule],
  templateUrl: './requests.component.html',
  styleUrl: './requests.component.scss'
})
export class RequestsComponent implements OnInit, OnDestroy {
  ref: DynamicDialogRef | undefined;
  user: any;
  listOfAccepted: any[] = [];
  listOfPending: any[] = [];
  initialTabIndex!: number | 0;
  imagePath: string = environment.SERVER;
  fetchingAcceptedRequests: boolean = false;
  fetchingPendingRequests: boolean = false;

  constructor(private activatedRoute: ActivatedRoute, private requestService: RequestService, private toastService: ToastService, private router: Router, private dialogService: DialogService) {
    this.activatedRoute.parent?.data.subscribe(({ user }) => {
      this.user = user;
    })
  }

  show(userId: number) {
    this.ref = this.dialogService.open(ProfileComponent, {
      width: '50vw',
      contentStyle: { overflow: 'auto' },
      baseZIndex: 10000, header: 'Profile', data: { enableEdit: false, userId }
    });
  }

  acceptedRequest() {
    this.fetchingAcceptedRequests = true;
    this.requestService.getAcceptedRequest().pipe(finalize(() => this.fetchingAcceptedRequests = false)).subscribe(
      (response: Request[]) => {
        this.listOfAccepted = response['body'];
      });
  }

  pendingRequest() {
    this.fetchingPendingRequests = true;
    this.requestService.getPendingRequest().pipe(finalize(() => this.fetchingPendingRequests = false)).subscribe(
      (response: Request[]) => {
        this.listOfPending = response['body'];
      });
  }


  updateRequest(userId: number, status: boolean) {
    this.requestService.updateRequest(userId, status).subscribe(
      (response) => {
        if (status) {
          this.toastService.showSuccess('Request accepted successfully!');
        } else {
          this.toastService.showSuccess('Request rejected successfully!');
        }
        this.pendingRequest()
      },
      (error) => {
        this.toastService.showError('Sonething Went Wrong!');
      }
    );
  }


  redirectToProfile(requestId: number): void {
    this.router.navigate(['other-profile', requestId], { state: { editUser: false } });
  }


  ngOnInit(): void {
    this.initialTabIndex = 0;
    if (this.user.role == 1) {
      this.acceptedRequest();
    }
    this.pendingRequest();
  }

  ngOnDestroy(): void {
    if (this.ref) {
      this.ref.close();
    }
  }
}
