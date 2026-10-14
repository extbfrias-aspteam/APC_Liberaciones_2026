import { ApplicationConfig, importProvidersFrom, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { JwtModule } from '@auth0/angular-jwt';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { AUTH_TOKEN } from './core/constants/auth.constants';
import { routes } from './app.routes';

import localeIt from '@angular/common/locales/es-MX';
import localeItExtra from '@angular/common/locales/extra/es-MX';

let lang = 'es-MX'
registerLocaleData(localeIt, lang, localeItExtra);

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()), 
    provideAnimations(),
    provideToastr(),
    importProvidersFrom(
      JwtModule.forRoot({
          config: {
              tokenGetter: () => {
                return localStorage.getItem(AUTH_TOKEN);
              },
              allowedDomains: ["localhost:8888"],
          },
      }),
    ),
    provideHttpClient(withInterceptors([
      authInterceptor
    ])),
    NgxUiLoaderService,
    { provide: LOCALE_ID, useValue: lang },
  ]
};