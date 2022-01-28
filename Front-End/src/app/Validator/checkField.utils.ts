import { FormGroup } from "@angular/forms";

export function checkField(form: FormGroup, controleName: string, error: string): boolean {
    let control = form.controls[controleName];
    if (control.hasError(error) && (control.touched || control.dirty)) {
      return true;
    }
    return false;
  }