import { DatePipe, NgClass } from '@angular/common';
import { AfterViewChecked, AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextareaModule } from "primeng/inputtextarea";
import { ListboxModule } from "primeng/listbox";
import { Message, MessagingService } from './messaging.service';
import { finalize, map, Subscription, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { OverlayService } from '../../services/overlay.service';
import { ActivatedRoute } from '@angular/router';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ProfileComponent } from '../profile/profile.component';
import { SkeletonModule } from 'primeng/skeleton';
import { BadgeModule } from "primeng/badge";

@Component({
  selector: 'rc-messaging',
  standalone: true,
  imports: [InputTextareaModule, FormsModule, ButtonModule, DatePipe, NgClass, ListboxModule, SkeletonModule, BadgeModule],
  templateUrl: './messaging.component.html',
  styleUrl: './messaging.component.scss',
  providers: [MessagingService]
})
export class MessagingComponent implements OnInit, AfterViewChecked, OnDestroy {
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;
  ref: DynamicDialogRef | undefined;
  message: string = "";
  selectedUser: any = null;
  listOfUsers: any[] = [];
  listOfMessages: Message[] = [];
  fetchingMessages: boolean = false;
  imagePath: string = environment.SERVER;
  messageListener$: Subscription = new Subscription();
  user: any;
  serverPath: string = environment.SERVER;

  constructor(private messagingService: MessagingService, private overlaySerivce: OverlayService, private actiavteRoute: ActivatedRoute, private dialogService: DialogService) {
    this.actiavteRoute.parent?.data.subscribe(({ user }) => {
      this.user = user;
    })
  }

  ngOnInit(): void {
    this.overlaySerivce.showOverlay("Fetching Users");
    this.messagingService.getAcceptedConnections()
      .pipe(
        finalize(() => this.overlaySerivce.hideOverlay()),
        map(response => { return { ...response, body: response['body'].map((user) => { return { ...user, newMessage: false } }) } })).subscribe((response) => {
          this.listOfUsers = response["body"];
        });

    this.messageListener$ = this.messagingService.receiveMessage(this.user.email).subscribe((data: Message) => {
      console.log(data);
      if (this.selectedUser.userId === data['senderId']) {
        this.listOfMessages.push(data);
        this.scrollToBottom();
      } else {
        this.listOfUsers.forEach(user => {
          if (user.userId === this.listOfMessages[0].senderId) {
            user.newMessage = true;
          }
        })
      }
    });
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  show(userId: number) {
    this.ref = this.dialogService.open(ProfileComponent, {
      width: '50vw',
      contentStyle: { overflow: 'auto' }, header: 'Profile', data: { enableEdit: false, userId }
    });
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }

  sendMessage() {
    if (this.message) {
      const messageBody: Message = {
        senderName: this.user.username, senderId: this.user.userId, senderEmail: this.user.email, receiverName: this.selectedUser.name, receiverEmail: this.selectedUser.email, receiverId: this.selectedUser.id, message: this.message, timestamp: new Date(), senderProfilePicture: this.user.profile
      }
      this.messagingService.sendMessage(messageBody);
      this.listOfMessages.push(messageBody);
      this.message = "";
    }
  }

  fetchMessages() {
    this.selectedUser.newMessage = false;
    this.fetchingMessages = true;
    this.messagingService.getChatHistory(this.selectedUser.email).pipe(finalize(() => this.fetchingMessages = false)).subscribe({
      next: response => {
        this.listOfMessages = response['body'];
      },
      error: () => {
        this.listOfMessages = []
      }
    })
  }

  ngOnDestroy(): void {
    this.messagingService.closeConnection();
    this.messageListener$.unsubscribe();
  }
}
