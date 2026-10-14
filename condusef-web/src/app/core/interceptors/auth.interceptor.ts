import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { EMPTY, catchError, throwError } from 'rxjs';
import { AuthService } from '../services';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // ToDo: se va a quitar
  //if (req.url.includes('https://api.condusef.gob.mx')) {
  //  return next(req);
  //}
  //

  const token = authService.token;
  const request = req.clone({
    setHeaders:{
      Authorization: `Bearer ${token}`
    }
  });
  
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si el error es 401 (Not Authorized), significa que el token ha caducado
      // y se cierra la sesión
      if (error && error.status === 401) {
        authService.cerrarSesion();
        return EMPTY;
      }

      return throwError(() => error);
    })
  );
};