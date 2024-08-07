import { Injectable } from "@angular/core";
import { BehaviorSubject, combineLatest, Observable } from "rxjs";

@Injectable({
    providedIn: "root"
})
export class OverlayService {
    private message: string = "";
    private overlay: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    getOverlay: Observable<boolean> = this.overlay.asObservable().pipe();

    showOverlay(message: string = "Loading") {
        this.message = message;
        this.overlay.next(true);
    }

    hideOverlay() {
        this.message = "";
        this.overlay.next(false);
    }

    get overlayMessage(): string {
        return this.message;
    }
}