import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';

export const routes: Routes = [
    {
        path: '',
        loadChildren: () => import('./pages/pages.routes').then(m => m.pages_routes)
    },
    {
        path: 'login',
        component: LoginComponent
    }
];
