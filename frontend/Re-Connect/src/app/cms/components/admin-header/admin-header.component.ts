import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { MenubarModule } from 'primeng/menubar';
import { AvatarModule } from "primeng/avatar";
import { environment } from '../../../../environments/environment';
import { ActivatedRoute } from '@angular/router';
import { ROLES } from '../../../shared/components/constants/roles';

@Component({
  selector: 'rc-admin-header',
  standalone: true,
  imports: [MenubarModule, AvatarModule],
  templateUrl: './admin-header.component.html',
  styleUrl: './admin-header.component.scss'
})
export class AdminHeaderComponent implements OnInit {
  items: MenuItem[] | undefined = [];
  loggedUser: any;
  profilePictureUrl: any;
  roles = ROLES;
  serverPath: string = environment.SERVER;
  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ user }) => {
      this.loggedUser = user;
    })
  }
}
