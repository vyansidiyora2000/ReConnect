import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ToastService } from './shared/services/toast.service';

@Component({
  selector: 'rc-root',
  standalone: true,
  imports: [RouterOutlet, ToastModule],
  template: `
    <router-outlet />
    <p-toast position="bottom-right"/>
  `,
  styles: [],
})
export class AppComponent {
  title = 'Re-Connect';
}
