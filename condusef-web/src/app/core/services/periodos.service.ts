import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, lastValueFrom, take } from 'rxjs';
import { Periodo } from '../models';
import { environment } from '../../../environments/environment';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class PeriodosService {

  private http = inject(HttpClient)
  
  constructor() { }

  obtenerPeriodos(schema: string): Observable<Periodo[]> {
    return this.http
        .get<Periodo[]>(`${environment.apiUrlBase}/api/v1/condusef/periodos?schema=${schema}`, { responseType: 'json' })
        .pipe(catchError(errorHandler));
  }

  obtenerPeriodoActivo(schema: string): Promise<Periodo> {
    const request$ = 
      this.http
        .get<Periodo>(`${environment.apiUrlBase}/api/v1/condusef/periodos/activo?schema=${schema}`, { responseType: 'json' })
        .pipe(take(1));

    return lastValueFrom<Periodo>(request$);
  }

  crearPeriodo(schema: string, formData: any): Observable<void> {
    return this.http
             .post<void>(`${environment.apiUrlBase}/api/v1/condusef/periodos?schema=${schema}`, formData, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  cerrarPeriodoActivo(schema: string, mes: number, anio: number): Observable<void> {
    return this.http
             .put<void>(`${environment.apiUrlBase}/api/v1/condusef/periodos/cerrar?schema=${schema}&mes=${mes}&anio=${anio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  abrirPeriodo(schema: string, mes: number, anio: number): Observable<void> {
    return this.http
             .put<void>(`${environment.apiUrlBase}/api/v1/condusef/periodos/abrir?schema=${schema}&mes=${mes}&anio=${anio}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }
}