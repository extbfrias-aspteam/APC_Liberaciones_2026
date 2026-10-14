import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { PagedResponse, ConsultaREUNE } from '../models/index';
import { environment } from '../../../environments/environment';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class ConsultasService {
  
  private readonly http = inject(HttpClient);

  constructor() { }

  obtenerConsultasPaginado(numTrim: number, offset: number, limit: number, filter: string): Observable<PagedResponse> {
    const params = new HttpParams()
    .append('num_trim', numTrim.toString())
    .append('offset', offset.toString())
    .append('limit' , limit.toString())
    .append('filter', filter);

    return this.http
             .get<PagedResponse>(`${environment.apiUrlBase}/api/v1/condusef/consultas/paginado`, { responseType: 'json', params })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerConsultaPorFolio(folio: string): Observable<ConsultaREUNE> {
    return this.http
             .get<ConsultaREUNE>(`${environment.apiUrlBase}/api/v1/condusef/consultas?num_folio=${folio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerConsultasSinReportar(numTrim: number): Observable<ConsultaREUNE[]> {
    return this.http
             .get<ConsultaREUNE[]>(`${environment.apiUrlBase}/api/v1/condusef/consultas/sin-reportar?num_trim=${numTrim}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  crearConsulta(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/consultas/crear`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarConsulta(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/consultas/editar`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarEstadoReportadoPorFolios(folios: string[], value: boolean) {
    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/consultas/reportar?folios=${folios.toString()}&reportado=${value}`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }
}