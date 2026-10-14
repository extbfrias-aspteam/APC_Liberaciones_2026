import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router      = inject(Router);

  if (!authService.esAutenticado()) {
    router.navigate(['/login'], {
        queryParams: {
            return: state.url
        }
    });
    return false;
  }
  return true;
};
