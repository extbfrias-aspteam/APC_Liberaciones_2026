import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { PagedResponse, ReclamacionREUNE } from '../models/index';
import { environment } from '../../../environments/environment';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class ReclamacionesService {
  
  private readonly http = inject(HttpClient);

  constructor() { }

  obtenerReclamacionesPaginado(numTrim: number, offset: number, limit: number, filter: string): Observable<PagedResponse> {
    const params = new HttpParams()
    .append('num_trim', numTrim.toString())
    .append('offset', offset.toString())
    .append('limit' , limit.toString())
    .append('filter', filter);

    return this.http
             .get<PagedResponse>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones/paginado`, { responseType: 'json', params })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerReclamacionPorFolio(folio: string): Observable<ReclamacionREUNE> {
    return this.http
             .get<ReclamacionREUNE>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones?num_folio=${folio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerReclamacionesSinReportar(numTrim: number): Observable<ReclamacionREUNE[]> {
    return this.http
             .get<ReclamacionREUNE[]>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones/sin-reportar?num_trim=${numTrim}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  crearReclamacion(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones/crear`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarReclamacion(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones/editar`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarEstadoReportadoPorFolios(folios: string[], value: boolean) {
    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones/reportar?folios=${folios.toString()}&reportado=${value}`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }
}