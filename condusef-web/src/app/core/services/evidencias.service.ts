import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Evidencia } from '../models';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class EvidenciasService {

  private http = inject(HttpClient)
  
  constructor() { }

  upload(file: File, folio: string): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file' , file);

    return this.http
      .post(`${environment.apiUrlBase}/api/v1/condusef/quejas/${folio}/evidencias/upload`, formData, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
      );
  }

  download(folio: string, id: string) {
    return this.http.get(`${environment.apiUrlBase}/api/v1/condusef/quejas/${folio}/evidencias/download?id=${id}`, { responseType: 'blob'});
  }

  obtenerEvidencias(folio: string): Observable<Evidencia[]>{
    return this.http
      .get<Evidencia[]>(`${environment.apiUrlBase}/api/v1/condusef/quejas/${folio}/evidencias`, { responseType: 'json' })
      .pipe(
        catchError(errorHandler)
    );
  }
}
