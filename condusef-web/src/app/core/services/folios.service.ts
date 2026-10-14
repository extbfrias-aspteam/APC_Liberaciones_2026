import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Folio } from '../models';
import { environment } from '../../../environments/environment';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class FoliosService {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  
  constructor() { }

  obtenerNumFolio(id: string): Promise<string> {
      return firstValueFrom(
        this.http
            .get<Folio>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/folios?id=${id}`, { responseType: 'json' })
      ).then((folio: Folio)=>{
        return folio.referenceID;
      });

    //return new Promise(async (resolve) => {
    //  this.http
    //    .get<Folio>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/folios?id=${id}`, { responseType: 'json' })
    //    .pipe(
    //      catchError(errorHandler)
    //     ).subscribe({
    //        next: (folio: Folio) => {
    //          resolve(folio.referenceID);  
    //        },
    //        error: (error) => {
    //          throw error;
    //        }
    //     });
    //});
  }

  async siguienteFolio(id: string): Promise<string> {
    const key = this.folioKey(id); 

    let numFolio = localStorage.getItem(key) ?? '';
    if (numFolio !== '') {
      return numFolio;
    }
      
    try {
      numFolio = await this.obtenerNumFolio(id);
      localStorage.setItem(key, numFolio);
      
      return numFolio;
    } catch(error) {
      
      throw error;
    }
  }

  desecharFolio(id: string) {
    const key = this.folioKey(id);
    
    localStorage.removeItem(key);
  }

  folioKey(id: string): string {
    let usuario = this.authService.usuarioAutenticado;
    let idEmpresa = usuario?.empresa.id;
    let key = `portal_emp${idEmpresa}_condusef_${id}_folio`

    return key;
  }
}
