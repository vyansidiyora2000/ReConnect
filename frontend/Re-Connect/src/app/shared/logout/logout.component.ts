import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'rc-logout',
  standalone: true,
  imports: [],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.scss'
})
export class LogoutComponent {
  constructor(private authService: AuthService) {
    this.authService.logout();
  }


}
