import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModal, NgbPaginationModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { QuejasConsultModalComponent, QuejasReportModalComponent } from '../..';
import { QuejasService, PeriodosService } from '../../../../core/services';
import { CondusefTokenRefreshModalComponent, EvidenciasUploadModalComponent } from '../../../shared';
import { PagedResponse, Periodo, Queja, QuejaEnvioResponse, QuejaEnvioResponseError, QuejaError } from '../../../../core/models';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-quejas-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NgbPaginationModule, RouterLink, NgbTooltipModule],
  templateUrl: './quejas-list.component.html',
  styleUrl: './quejas-list.component.scss'
})
export class QuejasListComponent implements OnInit, OnDestroy {

  private readonly quejasService  = inject(QuejasService);
  private readonly periodoService = inject(PeriodosService);
  private readonly modalService   = inject(NgbModal);
  private readonly toastr         = inject(ToastrService);
  private readonly ngxLoader      = inject(NgxUiLoaderService);
  private readonly router         = inject(Router);

  public filter: string;
  public offset: number;
  public limit: number;
  public maxPageSize: number;
  public total: number;
  public quejas: Queja[];
  public periodo: Periodo | undefined;
  
  private subscription?: Subscription;

  constructor() { 
    this.filter  = '';
    this.offset  = 1;
    this.limit   = 25;
    this.maxPageSize = 5;
    this.total   = 0;
    this.quejas  = [];
  }

  ngOnInit(): void {
    this.obtenerPeriodoActivo();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  /*
    Periodos
  */

  obtenerPeriodoActivo(): void {
    this.ngxLoader.start();
    this.periodoService.obtenerPeriodoActivo('redeco').then((periodo) => {
      this.periodo = periodo;
      this.ngxLoader.stop();

      this.obtenerQuejasPaginado();
    }, (error) => {
      this.ngxLoader.stop();
      this.toastr.info(`${error.error}`, 'Periodos');
    });
  }

  /*
    Quejas
  */
  
  obtenerQuejasPaginado(): void {
    if (!this.periodo) {
      this.toastr.info('Debe existir un periodo activo', 'Quejas');
      return;
    }

    this.ngxLoader.start();
    this.subscription = this.quejasService.obtenerQuejasPaginado(this.periodo.mes, this.offset, this.limit, this.filter).subscribe({
      next: (response: PagedResponse) => {
        this.quejas = response.items;
        this.total = response.total;
        this.ngxLoader.stop();
      },
      error: (error) => {
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        this.ngxLoader.stop();
      }
    });
  }

  reportarQueja(folio: string): void {
      this.ngxLoader.start();
      this.subscription = this.quejasService.reportarQuejasCondusef([folio]).subscribe({
        next: (response: QuejaEnvioResponse) => {
          // La/las queja(s) fueron reportadas correctamente
          this.actualizarEstadoReportado(folio, true);
          this.ngxLoader.stop();
          this.toastr.success(`${response['Número total de envios']} Queja(s) reportada satisfactoriamente`, 'Quejas');
        },
        error: (error) => {
          // Se trata el error par identificar si es un error generado en las quejas
          // o simplemente se trata de un error generico
          try {
            // Si se encontraron errores en 1 o mas folios, se procesa y se muestran en 
            // la pestaña errores.
            const obj: QuejaEnvioResponseError = JSON.parse(JSON.stringify(error));
            const keys: string[] = Object.keys(obj.errors);
            const errors: QuejaError[] = []; 
            
            keys.forEach(function (value) {
              var err: QuejaError = <QuejaError>{
                numFolio: value,
                errors: []
              };
                
              obj.errors[value].forEach(function (str: any) {
                err.errors.push(str)
              });

              errors.push(err);
            });

            this.ngxLoader.stop();
            this.toastr.error(`${obj.message}`, 'Ha ocurrido un error...');
            this.mostrarResultadoReportarQueja(folio, errors[0].errors);
          } catch(err){

            this.ngxLoader.stop();
            this.toastr.error(`${error}`, 'Ha ocurrido un error...');
          }
        }
      }); 
  }

  mostrarResultadoReportarQueja(folio: string, errors: string[]): void {
    var errorTitle = `El folio ${folio} tiene los siguientes errores:`;
    var errorMessage = '<ul style="text-align:left;">';
    errors.forEach(error => {
      if (error !== '') {
        errorMessage += `<li>${error}</li>`
      }
    });
    errorMessage += '</ul>'

    Swal.fire({icon: "error", title: errorTitle, html: errorMessage});
  }

  eliminarQuejaCondusef(folio: string): void {
    this.subscription = this.quejasService.eliminarQuejaCondusef(folio).subscribe({
      next: () => {
        this.actualizarEstadoReportado(folio, false);
        this.toastr.success('La queja ha sido eliminada correctamente del portal Condusef', 'Quejas');
      },
      error: (error) => {
        if (error.msg) {
          this.toastr.error(`${error.msg}`, 'Ha ocurrido un error...');
        } else {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      }
    });
  }

  eliminarQueja(folio: string): void {
    Swal.fire({
      title: 'Eliminar Queja',
      text: `¿Seguro que deseas eliminar la queja con numero de folio ${folio}?`,
      showCancelButton: true,
      confirmButtonText: 'Si, Deseo Eliminarla',
      cancelButtonText: 'No'
    }).then((result) => {
      if (result.value) {
        this.ngxLoader.start();
        this.subscription = this.quejasService.eliminarQueja(folio).subscribe({
          next: () => {
            this.obtenerQuejasPaginado();
            this.ngxLoader.stop();
            this.toastr.success("Queja eliminada satisfactoriamente", 'Quejas');
          },
          error: (error) => {
            this.ngxLoader.stop();
            this.toastr.error(`${error.message}`, 'Ha ocurrido un error...');
          }
        });
      }
    });
  }

  actualizarEstadoReportado(folio: string, value: boolean): void {
    this.ngxLoader.start();
    this.subscription = this.quejasService.actualizarEstadoReportado([folio], value).subscribe({
      next: () => { 
        let index = this.quejas.findIndex(item => item.numFolio === folio);
        this.quejas[index].reportado = value;
        this.ngxLoader.stop();
      },
      error: (error) => {
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        this.ngxLoader.stop();
      }
    });
  }

  navegarEditarQueja(queja: Queja): void {
    this.router.navigate(['/redeco/registrar'], 
      { 
        state: {
          'periodo': this.periodo, 
          'editar': true, 
          'queja': queja
        } 
      });
  }

  /*
    Ventanas Modal
  */

  modalReportarQuejas(): void {
    const modalRef = this.modalService.open(QuejasReportModalComponent, { backdrop: 'static', size: 'lg' });
    modalRef.componentInstance.paramPeriodo = this.periodo;
    modalRef.result.then(() => {
      this.obtenerQuejasPaginado();
    }, () => { });
  }

  modalConsultarQuejasREDECO(): void {
    const modalRef = this.modalService.open(QuejasConsultModalComponent, { backdrop: 'static', size: 'lg' });
    modalRef.componentInstance.paramPeriodo = this.periodo;
    modalRef.result.then(() => {
      
    }, () => { });
  }

  modalActualizarToken(): void {
    const modalRef = this.modalService.open(CondusefTokenRefreshModalComponent, { backdrop: 'static', size: 'lg' });
    modalRef.componentInstance.paramEsquema = 'redeco';
    modalRef.result.then(() => {
      this.toastr.success('El token ha sido actualizado satisfactoriamente', '');
    }, () => { });
  }

  modalEvidencias(queja: Queja): void {
    const modalRef = this.modalService.open(EvidenciasUploadModalComponent, { backdrop: 'static', size: 'lg' });
    modalRef.componentInstance.paramQueja = queja;
    modalRef.result.then(() => {
      
    }, () => { });
  }

  /*
   *  Paginación
  */

  paginationPageChange(): void {
    this.obtenerQuejasPaginado();
  }
}