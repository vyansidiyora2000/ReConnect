
import { Component, OnInit } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { NgForOf, NgIf } from "@angular/common";
import { AdminUsersService, UserNameTypeIdDTO } from './admin-users.service';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ROLES } from '../../../shared/components/constants/roles';
import { tap } from 'rxjs';
import { DropdownModule } from 'primeng/dropdown';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'rc-admin-users',
  standalone: true,
  imports: [DialogModule, NgIf, NgForOf, TableModule, ButtonModule, DropdownModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss'],
  providers: [AdminUsersService]
})
export class AdminUsersComponent implements OnInit {
  users: UserNameTypeIdDTO[] = [];
  displayDeleteDialog = false;
  userToDelete: UserNameTypeIdDTO | null = null;
  roles: any = ROLES;
  loadingUsers: boolean = false;
  dropdownRoles: any;
  selectedRole: string;

  constructor(private userService: AdminUsersService) {
    this.dropdownRoles = Object.values(this.roles);
    this.selectedRole = this.roles["1"];
  }

  ngOnInit() {
    this.loadUsers(1);
  }

  private loadUsers(typeId: number) {
    this.loadingUsers = true;
    this.userService.getAllUsersByTypeId(typeId).pipe(tap(() => this.loadingUsers = false)).subscribe(
      (users) => this.users = users,
      (error) => console.error('Error loading users:', error)
    );
  }

  showDeleteUserConfirmation(user: UserNameTypeIdDTO) {
    this.userToDelete = user;
    this.displayDeleteDialog = true;
  }

  hideDeleteDialog() {
    this.displayDeleteDialog = false;
    this.userToDelete = null;
  }

  onDeleteUser() {
    if (this.userToDelete) {
      this.userService.deleteUser(this.userToDelete.userId).subscribe(
        () => {
          // this.loadUsers('Admin');
          this.hideDeleteDialog();
        },
        (error) => console.error('Error deleting user:', error)
      );
    }
  }

  onRoleChange(typeName: string) {
    let typeId;
    for (const [key, value] of Object.entries(this.roles)) {
      if (typeName === value) typeId = key;
    }
    this.loadUsers(parseInt(typeId));
  }
}