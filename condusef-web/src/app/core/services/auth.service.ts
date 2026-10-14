import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable, catchError } from 'rxjs';
import { AUTH_TOKEN } from '../constants/auth.constants';
import { AuthUser, JsonResponse } from '../models';
import { errorHandler } from '../../app.helpers';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http      = inject(HttpClient);
  private jwtHelper = inject(JwtHelperService);
  private router    = inject(Router);
  
  constructor() { }

  get token(): string | null {
    return localStorage.getItem(AUTH_TOKEN);
  }

  get usuarioAutenticado(): AuthUser | null {
    if (this.token) {
      return this.jwtHelper.decodeToken<AuthUser>(this.token);
    }

    return null
  }

  autenticar(username: string, password: string, idEmpresa: string): Observable<JsonResponse> {
    const headers = new HttpHeaders()
      .append('Content-Type', 'application/x-www-form-urlencoded');

    const body = new HttpParams()
      .append('username', username)
      .append('password', password)
      .append('idEmpresa' , idEmpresa);

    return this.http
      .post<JsonResponse>(`${environment.apiUrlBase}/api/v1/condusef/login`, body, { responseType: 'json', headers })
      .pipe(catchError(errorHandler));
  }

  guardarToken(token: string): void {
    localStorage.setItem(AUTH_TOKEN, token);
  }

  esAutenticado(): boolean {
    if (!this.token) {
      return false;  
    }

    return !this.jwtHelper.isTokenExpired(this.token);
  }

  cerrarSesion(): void {
    const user = this.usuarioAutenticado;
    this.http
      .post(`${environment.apiUrlBase}/api/v1/condusef/logout`, user, { responseType: 'json' })
      .subscribe({
        next: () => {
          localStorage.removeItem(AUTH_TOKEN);
          this.router.navigate(['login']);
        },
        error: () => {
          localStorage.removeItem(AUTH_TOKEN);
          this.router.navigate(['login']);
        }
      });
  }
}
