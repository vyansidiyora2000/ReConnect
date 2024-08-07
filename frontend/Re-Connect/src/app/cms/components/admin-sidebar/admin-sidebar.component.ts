import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api/menuitem';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { AuthService } from '../../../../app/shared/services/auth.service';

@Component({
  selector: 'rc-admin-sidebar',
  standalone: true,
  imports: [MenuModule, ButtonModule],
  templateUrl: './admin-sidebar.component.html',
  styleUrl: './admin-sidebar.component.scss'
})
export class AdminSidebarComponent implements OnInit {
  items: MenuItem[] | undefined;
  user: any;

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute, private activatedRoute: ActivatedRoute) {
    this.activatedRoute.data.subscribe(({ user }) => {
      this.user = user;
    })
  }

  ngOnInit() {
    this.items = [
      {
        label: "Dashboard",
        icon: "pi pi-home",
        route: "/admin/dashboard"
      },
      {
        label: "Countries",
        icon: "pi pi-globe",
        route: "/admin/countries"
      },
      {
        label: "Skills",
        icon: "pi pi-map-marker",
        route: "/admin/skills"
      },
      {
        label: "SkillDomain",
        icon: "pi pi-map-marker",
        route: "/admin/skill-domain"
      },
      {
        label: "Users",
        icon: "pi pi-users",
        route: "/admin/users"
      },
      {
        label: "Companies",
        icon: "pi pi-building",
        route: "/admin/companies",
      }
    ]
  }
}
