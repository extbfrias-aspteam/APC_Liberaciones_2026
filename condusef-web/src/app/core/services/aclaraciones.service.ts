import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { PagedResponse, AclaracionREUNE } from '../models/index';
import { environment } from '../../../environments/environment';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class AclaracionesService {
  
  private readonly http = inject(HttpClient);

  constructor() { }

  obtenerAclaracionesPaginado(numTrim: number, offset: number, limit: number, filter: string): Observable<PagedResponse> {
    const params = new HttpParams()
    .append('num_trim', numTrim.toString())
    .append('offset', offset.toString())
    .append('limit' , limit.toString())
    .append('filter', filter);

    return this.http
             .get<PagedResponse>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones/paginado`, { responseType: 'json', params })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerAclaracionPorFolio(folio: string): Observable<AclaracionREUNE> {
    return this.http
             .get<AclaracionREUNE>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones?num_folio=${folio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerAclaracionesSinReportar(numTrim: number): Observable<AclaracionREUNE[]> {
    return this.http
             .get<AclaracionREUNE[]>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones/sin-reportar?num_trim=${numTrim}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  crearAclaracion(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones/crear`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarAclaracion(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones/editar`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarEstadoReportadoPorFolios(folios: string[], value: boolean) {
    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones/reportar?folios=${folios.toString()}&reportado=${value}`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }
}