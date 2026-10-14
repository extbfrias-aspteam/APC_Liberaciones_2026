import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map } from 'rxjs';
import { Producto, Causa, EntidadFed, Municipio, NivelAtencion, MedioRecepcion, CodigoPostal, Colonia, Empresa } from '../models/index';
import { environment } from '../../../environments/environment';
import { errorHandler } from '../../app.helpers';

@Injectable({
  providedIn: 'root'
})
export class CatalogosService {

  private http = inject(HttpClient)

  constructor() { }

  obtenerEntidadesFed(): Observable<EntidadFed[]> {
    return this.http
             .get<EntidadFed[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/sepomex/entidades`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerMunicipios(idEntidadFed: string, codigoPostal: string): Observable<Municipio[]> {
    return this.http
             .get<Municipio[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/sepomex/municipios?idEntidadFed=${idEntidadFed}&cp=${codigoPostal}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerCodigosPostales(idEntidadFed: string): Observable<CodigoPostal[]> {
    return this.http
             .get<CodigoPostal[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/sepomex/codigos-postales?idEntidadFed=${idEntidadFed}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerColonias(codigoPostal: string): Observable<Colonia[]> {
    return this.http
             .get<Colonia[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/sepomex/colonias?cp=${codigoPostal}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerProductos(schema: string): Observable<Producto[]> {
    return this.http
             .get<Producto[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/productos?schema=${schema}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerCausas(idProducto: string, schema: string, tipo: number = 0): Observable<Causa[]> {
    return this.http
            .get<Causa[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/causas?idProducto=${idProducto}&tipo=${tipo}&schema=${schema}`, { responseType: 'json' })
            .pipe(
                map((res) => {
                  return res;
                }),
                catchError(errorHandler)
            );
  }

  obtenerMediosRecepcion(schema: string): Observable<MedioRecepcion[]> {
    return this.http
             .get<MedioRecepcion[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/medios-recepcion?schema=${schema}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerNivelesAtencion(schema: string): Observable<NivelAtencion[]> {
    return this.http
             .get<NivelAtencion[]>(`${environment.apiUrlBase}/api/v1/condusef/catalogos/niveles-atencion?schema=${schema}`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }

  obtenerEmpresas(): Observable<Empresa[]> {
    return this.http
             .get<Empresa[]>(`${environment.apiUrlBase}/api/v1/condusef/login/empresas`, { responseType: 'json' })
             .pipe(
                catchError(errorHandler)
            );
  }
}