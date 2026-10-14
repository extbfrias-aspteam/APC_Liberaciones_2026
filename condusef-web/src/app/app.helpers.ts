/*
 * js helpers:
 *
 * correctHeight() - fix the height of main wrapper
 * detectBody() - detect windows size
 * errorHandler() -
 * dateStringToString_yyyy_mm_dd() - transforma las fechas que vienen en formato dd/MM/yyyy
 *                                   a formato yyyy/MM/dd para su correcta asignacion usando new Date()
 */

import { HttpErrorResponse } from "@angular/common/http";
import { FormGroup } from "@angular/forms";
import { NgbDateStruct } from "@ng-bootstrap/ng-bootstrap";
import { Observable, throwError } from "rxjs";

declare var $: any;
declare var window: any;

export function correctHeight(): void {
  const pageWrapper   = $('#page-wrapper');
  const navbarHeight  = $('nav.navbar-default').height();
  const wrapperHeight = pageWrapper.height();

  if (navbarHeight > wrapperHeight) {
    pageWrapper.css('min-height', navbarHeight + 'px');
  }

  if (navbarHeight <= wrapperHeight) {
    if (navbarHeight < $(window).height()) {
      pageWrapper.css('min-height', $(window).height() + 'px');
    } else {
      pageWrapper.css('min-height', navbarHeight + 'px');
    }
  }

  if ($('body').hasClass('fixed-nav')) {
    if (navbarHeight > wrapperHeight) {
      pageWrapper.css('min-height', navbarHeight + 'px');
    } else {
      pageWrapper.css('min-height', $(window).height() - 60 + 'px');
    }
  }
}

export function detectBody(): void {
  if ($(document).width() < 769) {
    $('body').addClass('body-small');
  } else {
    $('body').removeClass('body-small');
  }
}

export function smoothlyMenu(): void {
  if (!$('body').hasClass('mini-navbar') || $('body').hasClass('body-small')) {
    // Hide menu in order to smoothly turn on when maximize menu
    $('#side-menu').hide();
    // For smoothly turn on menu
    setTimeout(
      () => {
        $('#side-menu').fadeIn(300);
      }, 200);
  } else if ($('body').hasClass('fixed-sidebar')) {
    $('#side-menu').hide();
    setTimeout(
      () => {
        $('#side-menu').fadeIn(300);
      }, 200);
  } else {
    // Remove all inline style from jquery fadeIn function to reset menu state
    $('#side-menu').removeAttr('style');
  }
}

export function errorHandler(error: HttpErrorResponse): Observable<never> {
  if (error.status === 0) {
    console.error('An error occurred:', error.error);
  } else {
    console.error(
      `Backend returned code ${error.status}, body was: `, error.error);
  }

  return throwError(() => error.error);
}

// Deprecated method, change by dateStringToString_yyyy_mm_dd
export function reorderDateString(date: string): string {
  if (date === "") return "";

  let dateSplit = date.split("/")
  return `${dateSplit[2]}/${dateSplit[1]}/${dateSplit[0]}`;
}

export function dateStringToString_yyyy_mm_dd(date: string): string {
  if (date === "") return "";

  let dateSplit = date.split("/")
  return `${dateSplit[2]}/${dateSplit[1]}/${dateSplit[0]}`;
}
export function isValidNgbDateStruct(date: NgbDateStruct): boolean {
  const { year, month, day } = date;
  
  if (typeof year !== 'number' || year < 1) return false;
  if (typeof month !== 'number' || month < 1 || month > 12) return false;
  if (typeof day !== 'number' || day < 1 || day > 31) return false;
  
  return true;
}
export function ngbDateToString_yyyy_mm_dd(date: NgbDateStruct) {
  if (!date || !isValidNgbDateStruct(date)) {
    return null;
  }
  return `${date.year}-${date.month.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}`;
}
export function String_yyyy_mm_ddToNgbDate(fecha: string): NgbDateStruct {
  const [year, month, day] = fecha.split('-').map(Number);
  return { year, month, day };
}
export function String_dd_yy_yyyyToNgbDate(fecha: string): NgbDateStruct {
  const [day, month, year] = fecha.split('/').map(Number);
  return { year, month, day };
}
export function ngbDateStructToDate(date: NgbDateStruct): Date {
  return new Date(date.year, date.month - 1, date.day);
}
export function isDateGreater(date1: NgbDateStruct, date2: NgbDateStruct): boolean {
  const d1 = ngbDateStructToDate(date1);
  const d2 = ngbDateStructToDate(date2);
  return d1.getTime() > d2.getTime();
}
export function atLeastOneCheckboxCheckedValidator(minRequired = 1) {
  return function validate(formGroup: FormGroup) {
    let checkedCount = 0;

    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.controls[key];
      if (control.value === true) {
        checkedCount++;
      }
    });

    return checkedCount >= minRequired ? null : { atLeastOneChecked: true };
  };
}