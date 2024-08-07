import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';
import { OverlayService } from '../../services/overlay.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'rc-layout',
  standalone: true,
  imports: [HeaderComponent, SidebarComponent, RouterOutlet, AsyncPipe],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent {
  constructor(protected overlayService: OverlayService) { }
}
