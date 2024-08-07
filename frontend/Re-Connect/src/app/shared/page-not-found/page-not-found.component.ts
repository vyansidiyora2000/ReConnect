import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'rc-page-not-found',
  standalone: true,
  imports: [ButtonModule],
  templateUrl: './page-not-found.component.html',
  styleUrl: './page-not-found.component.scss'
})
export class PageNotFoundComponent {
  redirectFrom?: string = "";

  constructor(private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation && navigation.previousNavigation) {
      this.redirectFrom = navigation.previousNavigation.finalUrl?.toString();
    }
  }

  goBack(): void {
    if (this.redirectFrom) {
      this.router.navigate([this.redirectFrom]);
    } else {
      this.router.navigate(['/']);
    }
  }
}
