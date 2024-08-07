import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { TooltipModule } from 'primeng/tooltip';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'rc-sidebar',
  standalone: true,
  imports: [MenuModule, ButtonModule, TooltipModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  items: MenuItem[] | undefined;
  user: any;

  constructor(private authService: AuthService, private activatedRoute: ActivatedRoute) {
    this.activatedRoute.data.subscribe(({ user }) => {
      this.user = user;
    })
  }

  ngOnInit() {
    this.items = [
      {
        label: "Homepage",
        icon: "pi pi-home",
        route: "/homepage",
      },
      // {
      //   label: "Notifications",
      //   icon: "pi pi-bell",
      //   route: "/notifications"
      // },
      {
        label: "Messages",
        icon: "pi pi-inbox",
        route: "/messages"
      },
      {
        label: "Requests",
        icon: "pi pi-users",
        route: "/requests"
      }
    ]

    // if(this.user.role == 2) {
    //   this.items.unshift({
    //     label: "Homepage",
    //     icon: "pi pi-search",
    //     route: "/homepage",
    //   })
    // }
  }
}
