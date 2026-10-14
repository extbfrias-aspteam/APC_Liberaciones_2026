import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ConsultaREUNE, ReclamacionREUNE, AclaracionREUNE } from '../models';
import { PagedResponse } from '../models/index';
import { errorHandler } from '../../app.helpers';
import { Registro } from '../models/registro.model';

@Injectable({
  providedIn: 'root'
})
export class ReuneService {

  private http = inject(HttpClient)
  
  constructor() { }
  obtenerPaginado(numTrim: number, offset: number, limit: number, filter: string): Observable<PagedResponse> {
    const params = new HttpParams()
    .append('num_trim', numTrim.toString())
    .append('offset', offset.toString())
    .append('limit' , limit.toString())
    .append('filter', filter);

    return this.http
             .get<PagedResponse>(`${environment.apiUrlBase}/api/v1/condusef/reune/paginado`, { responseType: 'json', params })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerPendientes(numTrim: number): Observable<Registro[]> {
    const params = new HttpParams()
    .append('num_trim', numTrim.toString());

    return this.http
             .get<Registro[]>(`${environment.apiUrlBase}/api/v1/condusef/reune/sin-reportar`, { responseType: 'json', params })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerConsultaPorFolio(folio: string): Observable<ConsultaREUNE> {
    return this.http
             .get<ConsultaREUNE>(`${environment.apiUrlBase}/api/v1/condusef/consultas?folio=${folio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerAclaracionPorFolio(folio: string): Observable<AclaracionREUNE> {
    return this.http
             .get<AclaracionREUNE>(`${environment.apiUrlBase}/api/v1/condusef/aclaraciones?folio=${folio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerReclamacionPorFolio(folio: string): Observable<ReclamacionREUNE> {
    return this.http
             .get<ReclamacionREUNE>(`${environment.apiUrlBase}/api/v1/condusef/reclamaciones?folio=${folio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  reportarRegistros(form: object): Observable<any> {

    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    
    return this.http
             .post(`${environment.apiUrlBase}/api/v1/condusef/reune/reportar`, form, { headers: headers, responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  
  eliminarRegistro(folio: string, tipo: number): Observable<void> {
    let registro = ""
    switch (tipo) {
      case 1:
        registro = "consultas"
        break;
      case 2:
        registro = "reclamaciones"
        break;
      case 3:
        registro = "aclaraciones"
        break;
      default:
        registro = "consultas"
        break;
    }
    return this.http
      .delete<void>(`${environment.apiUrlBase}/api/v1/condusef/${registro}/${folio}`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }
}
