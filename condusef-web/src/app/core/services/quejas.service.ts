import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map } from 'rxjs';
import { PagedResponse, QuejaEnvioResponse, QuejaREDECO, QuejaReportadaItem, QuejaReportadaResponse } from '../models/index';
import { errorHandler } from '../../app.helpers'
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class QuejasService {
  
  private readonly http = inject(HttpClient);

  constructor() { }

  obtenerQuejasPaginado(numMes: number, offset: number, limit: number, filter: string): Observable<PagedResponse> {
    const params = new HttpParams()
    .append('num_mes', numMes.toString())
    .append('offset', offset.toString())
    .append('limit' , limit.toString())
    .append('filter', filter);

    return this.http
             .get<PagedResponse>(`${environment.apiUrlBase}/api/v1/condusef/quejas/paginado`, { responseType: 'json', params })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerQuejasSinReportar(numMes: number): Observable<QuejaREDECO[]> {
    return this.http
             .get<QuejaREDECO[]>(`${environment.apiUrlBase}/api/v1/condusef/quejas/sin-reportar?num_mes=${numMes}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  crearQueja(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .post<void>(`${environment.apiUrlBase}/api/v1/condusef/quejas`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  modificarQueja(form: string): Observable<void> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }

    return this.http
      .put<void>(`${environment.apiUrlBase}/api/v1/condusef/quejas`, form, httpOptions)
      .pipe(
        catchError(errorHandler)
      );
  }

  eliminarQueja(folio: string): Observable<void> {

    return this.http
      .delete<void>(`${environment.apiUrlBase}/api/v1/condusef/quejas/${folio}`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }

  actualizarEstadoReportado(folios: string[], value: boolean) {
    return this.http
      .put<void>(`${environment.apiUrlBase}/api/v1/condusef/quejas/reportar/cambiar-estado?folios=${folios.toString()}&reportado=${value}`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }

  obtenerQuejasReportadasCondusef(mes: number, anio: number): Observable<QuejaReportadaItem[]> {
    return this.http
             .get<QuejaReportadaResponse>(`${environment.apiUrlBase}/api/v1/condusef/quejas/reportadas?anio=${anio}&mes=${mes}`, { responseType: 'json' })
             .pipe(
                map((res) => res.quejas),
                catchError(errorHandler)
            );
  }

  reportarQuejasCondusef(folios: object): Observable<any> {
    return this.http
             .post<QuejaEnvioResponse>(`${environment.apiUrlBase}/api/v1/condusef/quejas/reportar`, folios, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  eliminarQuejaCondusef(folio: string): Observable<any> {
    return this.http
            .delete(`${environment.apiUrlBase}/api/v1/condusef/quejas/eliminar?folio=${folio}`, { responseType: 'json' })
            .pipe(
                catchError(errorHandler)
            );
  }
}