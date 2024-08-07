import { AbstractControl } from '@angular/forms';

declare module '@angular/forms' {
    interface AbstractControl {
        markAsDisabled(disable: boolean, onlySelf?: boolean): void;
        markAllAsDisabled(disable: boolean, onlySelf?: boolean): void;
        markAllAsDirty(): void;
    }
}

function markAsDisabled(this: any, disable: boolean, onlySelf?: boolean): void {
    if (disable) {
        this.disable();
    } else {
        this.enable();
    }

    if (this._parent && !onlySelf) {
        this._parent.markAsDisabled(disable, onlySelf);
    }
}

function markAllAsDisabled(this: any, disable: boolean, onlySelf?: boolean): void {
    this.markAsDisabled(disable, onlySelf);

    Object.keys(this.controls).forEach(k => {
        (this.controls[k] as AbstractControl).markAsDisabled(disable, onlySelf);
    });
}

function markAllAsDirty(this: any): void {
    this.markAsDirty();

    Object.keys(this.controls).forEach(k => {
        (this.controls[k] as AbstractControl).markAsDirty();
    });
}

AbstractControl.prototype.markAsDisabled = markAsDisabled;
AbstractControl.prototype.markAllAsDisabled = markAllAsDisabled;
AbstractControl.prototype.markAllAsDirty = markAllAsDirty;