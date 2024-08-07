import { HttpClient, HttpParams } from "@angular/common/http";
import { environment } from "../../../../environments/environment";
import { Injectable } from "@angular/core";
import { io, Socket } from "socket.io-client";
import { Observable } from "rxjs";

export interface Message {
    message: string;
    senderId: number;
    senderName: string;
    senderProfilePicture?: string;
    senderEmail: string;
    receiverId: number;
    receiverName: string;
    receiverEmail: string;
    receiverProfilePicture?: string;
    timestamp: Date;
    read?: boolean;
}

@Injectable()
export class MessagingService {
    private socket: Socket;
    constructor(private http: HttpClient) {
        this.socket = io(environment.SOCKET_SERVER);
    }

    closeConnection() {
        if (this.socket) {
            this.socket.disconnect();
        }
    }

    getAcceptedConnections() {
        return this.http.get<any[]>(environment.API + "getAcceptedConnections");
    }

    getChatHistory(email: string) {
        let params = new HttpParams();
        params = params.append("email", email);
        return this.http.get<Message[]>(environment.API + "getChatHistory", { params: params });
    }

    sendMessage(message: Message) {
        this.socket.emit('messageSendToUser', message);
    }

    receiveMessage(userEmail: string): Observable<Message> {
        return new Observable(observer => {
            this.socket?.on(userEmail, (data: any) => {
                console.log(data);
                observer.next(data);
            });
            return () => { this.socket.disconnect(); };
        });
    }
}