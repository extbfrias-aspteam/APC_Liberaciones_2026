import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthuserCONDUSEFResponse } from '../models';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class RedecoService {

  private http = inject(HttpClient)
  
  constructor() { }

  iniciarSesionCONDUSEF(json: string): Observable<AuthuserCONDUSEFResponse> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json;charset=UTF-8');

    return this.http
             .post<any>(`${environment.apiUrlBase}/api/v1/condusef/portal/token`, json, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }
}
