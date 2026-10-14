import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VerticalLayoutComponent } from '../layout/vertical-layout/vertical-layout.component';
import { QuejasListComponent } from './redeco/quejas/quejas-list/quejas-list.component';
import { QuejasCreateComponent } from './redeco/quejas/quejas-create/quejas-create.component';
import { ReuneListComponent} from './reune/reune-list/reune-list.component';
import {ConsultasReuneCreateComponent} from './reune/consultas-create/consultas-create.component';
import {AclaracionesReuneCreateComponent} from './reune/aclaraciones-create/aclaraciones-create.component';
import {ReclamacionesReuneCreateComponent} from './reune/reclamaciones-create/reclamaciones-create.component';
import { authGuard } from '../core/guards/auth.guard';
import { PeriodosListComponent } from './shared/periodos/periodos-list/periodos-list.component';

export const pages_routes: Routes = [
    {
        path: '',
        component: VerticalLayoutComponent,
        canActivate: [authGuard],
        children: [
            {
                path: 'redeco',
                title: 'Registro de Despachos de Cobranza',
                data: {
                    breadcrumb: 'REDECO'
                },
                children: [
                    {
                        path:'consultar',
                        title: 'Consultar quejas - Registro de Despachos de Cobranza',
                        data: {
                            breadcrumb: 'Consultar quejas'
                        },
                        component: QuejasListComponent
                    },
                    {
                        path:'registrar',
                        title: 'Registrar quejas - Registro de Despachos de Cobranza',
                        data: {
                            breadcrumb: 'Registrar quejas'
                        },
                        component: QuejasCreateComponent
                    },
                    {
                        path:'periodos/:schema',
                        title: 'Periodos - Registro de Despachos de Cobranza',
                        data: {
                            breadcrumb: 'Periodos'
                        },
                        component: PeriodosListComponent
                    }
                ]
            },
            {
                path: 'reune',
                title: 'Registro de Información de Unidad Especializada',
                data: {
                    breadcrumb: 'REUNE'
                },
                children: [
                    {
                        path:'consultar',
                        title: 'Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Resumen'
                        },
                        component: ReuneListComponent
                    },
                    {
                        path:'consultas/registrar',
                        title: 'Registrar consultas - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Registrar consultas'
                        },
                        component: ConsultasReuneCreateComponent
                    },
                    {
                        path:'aclaraciones/registrar',
                        title: 'Registrar aclaraciones - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Registrar aclaraciones'
                        },
                        component: AclaracionesReuneCreateComponent
                    },
                    {
                        path:'reclamaciones/registrar',
                        title: 'Registrar reclamaciones - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Registrar reclamaciones'
                        },
                        component: ReclamacionesReuneCreateComponent
                    },
                    {
                        path:'consultas/editar',
                        title: 'Editar consulta - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Editar consulta'
                        },
                        component: ConsultasReuneCreateComponent
                    },
                    {
                        path:'aclaraciones/editar',
                        title: 'Editar aclaraciones - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Editar aclaración'
                        },
                        component: AclaracionesReuneCreateComponent
                    },
                    {
                        path:'reclamaciones/editar',
                        title: 'Editar reclamación - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Editar reclamacióm'
                        },
                        component: ReclamacionesReuneCreateComponent
                    },
                    {
                        path:'trimestres/:schema',
                        title: 'Trimestres - Registro de Información de Unidad Especializada',
                        data: {
                            breadcrumb: 'Trimestres'
                        },
                        component: PeriodosListComponent
                    }
                ]
            },
        ]
    },
];

@NgModule({
    imports: [RouterModule.forRoot(pages_routes)],
    exports: [RouterModule]
  })
  export class AppRoutingModule { }