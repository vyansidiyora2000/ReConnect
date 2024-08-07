import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { InputGroupModule } from 'primeng/inputgroup';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ROLES } from '../constants/roles';
import { environment } from '../../../../environments/environment';
import { ProfileService } from "../profile/profile.service";

@Component({
  selector: 'rc-header',
  standalone: true,
  imports: [InputGroupModule, ButtonModule, RouterLink, InputTextModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
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
