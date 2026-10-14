import { Component, OnDestroy, OnInit, inject  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { ReuneReportModalComponent } from '../reune-report-modal/reune-report-modal.component';
import { ReuneService, PeriodosService } from '../../../core/services';
import { AclaracionREUNE, ConsultaREUNE, PagedResponse, Periodo, ReclamacionREUNE} from '../../../core/models';
import { Subscription, Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { NgbModal, NgbPaginationModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { Registro, ResponseCondusef } from '../../../core/models/registro.model';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { CondusefApiResponseComponent, CondusefTokenRefreshModalComponent, PeriodoCreateModalComponent } from '../../shared';


@Component({
  selector: 'app-reune-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NgbPaginationModule, RouterLink, NgbDropdownModule, NgbTooltipModule],
  templateUrl: './reune-list.component.html',
  styleUrl: './reune-list.component.scss'
})
export class ReuneListComponent implements OnInit, OnDestroy {
  public searchText: string = '';
  public offset: number = 1;
  public limit: number = 25;
  public maxPageSize: number = 5;
  public total: number = 0;
  public registros: Registro[] = [];
  public periodo: Periodo | undefined;
  public registros$: Observable<Registro[]>;
  
  private subscription?: Subscription;
  
  private reuneService  = inject(ReuneService);
  private periodoService = inject(PeriodosService);
  private modalService   = inject(NgbModal);
  private toastr         = inject(ToastrService);
  private ngxLoader      = inject(NgxUiLoaderService);
  private router         = inject(Router);
  public filter = new FormControl('', { nonNullable: true });

  constructor() {
    this.registros$ = this.filter.valueChanges.pipe(
			startWith(''),
			map((text) => this.search(text)),
		);
  }

  ngOnInit(): void {
    this.obtenerPeriodoActivo();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  /*
    Métodos
  */
    
    search(text: string): Registro[] {
      this.searchText = text;
      return this.registros.filter((registro) => {
        const term = text.toLowerCase();
        return (
          registro.folio.toLowerCase().includes(term) ||
          registro.producto.toLowerCase().includes(term) ||
          registro.subproducto.toLowerCase().includes(term)
        );
      });
    }

  obtenerPeriodoActivo(): void {
    this.ngxLoader.start();
    this.periodoService.obtenerPeriodoActivo('reune').then((periodo) => {
      this.periodo = periodo;
      this.ngxLoader.stop();

      this.obtenerPaginado();
    }, (error) => {
      this.ngxLoader.stop();
      this.toastr.error(`${error.error}`, 'Ha ocurrido un error...');
    });
  }

  crearPeriodoActivo(): void {
    const modalRef = this.modalService.open(PeriodoCreateModalComponent, { backdrop: 'static', size: 'md' });
    modalRef.componentInstance.tipo = 3;
    modalRef.result.then((formData: any) => {
      this.subscription = this.periodoService.crearPeriodo('reune', formData).subscribe({
        next: () => {
          this.obtenerPeriodoActivo();
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
    }, () => { });
  }

  cerrarPeriodoActivo(): void {
    if (!this.periodo) {
      console.log("Debe haber un periodo activo");
      return;
    }

    Swal.fire({
      title: 'Cerrar trimestre',
      text: '¿Seguro que deseas cerrar el trimestre seleccionado?',
      showCancelButton: true,
      confirmButtonText: 'Si, deseo cerrarlo',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.value) {
        if (!this.periodo) {
          return;
        }
        
        this.ngxLoader.start();
        this.subscription = this.periodoService.cerrarPeriodoActivo('reune', this.periodo?.mes, this.periodo?.anio).subscribe({
          next: () => {
            this.periodo = undefined;
            this.registros  = [];
            this.total   = 0;
            this.ngxLoader.stop();
          },
          error: (error) => {
            this.ngxLoader.stop();
            this.toastr.error(`${error}`, 'Ha ocurrido un error...');
          }
        });
      }
    });
  }
  reportarRegistros(): void {
    this.ngxLoader.start();
    this.subscription = this.reuneService.obtenerPendientes(this.periodo!.mes).subscribe({
      next: (response: Registro[]) => {
        this.ngxLoader.stop();
        const modalRef = this.modalService.open(ReuneReportModalComponent, { backdrop: 'static', size: 'lg' });
        modalRef.componentInstance.paramPeriodo = this.periodo;
        modalRef.componentInstance.registros = response;
      },
      error: (error) => {
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      }
    });
    
  }

  reportarRegistro(folio: String): void {
    this.ngxLoader.start();

    this.subscription = this.reuneService.reportarRegistros({folios: [folio]}).subscribe({
      next: (response: ResponseCondusef) => {
        this.ngxLoader.stop();
        const modalRef = this.modalService.open(CondusefApiResponseComponent, { backdrop: 'static', size: 'lg' });
        modalRef.componentInstance.apiResponse = response;
      },
      error: (error) => {
        this.ngxLoader.stop();
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      }
    });
  }

  actualizarToken(): void {
    const modalRef = this.modalService.open(CondusefTokenRefreshModalComponent, { backdrop: 'static', size: 'lg' });
    modalRef.componentInstance.paramEsquema = 'reune';
    modalRef.result.then(() => {
      this.toastr.success('El token ha sido actualizado satisfactoriamente', '');
    }, () => { });
  }

  navegarEditar(folio: string, tipo: number): void {
    var modulo = '';
    if(tipo == 1){ 
      this.ngxLoader.start();
      this.obtenerConsultaPorFolio(folio).then((registro) => {
        this.ngxLoader.stop();
        this.router.navigate(['/reune/consultas/editar'], 
          { 
            state: {
              'periodo': this.periodo, 
              'editar': true, 
              'folio': folio,
               'registro': registro
            } 
          });
      });
        
    }else if(tipo == 2){ 
      this.ngxLoader.start();
      this.obtenerReclamacionPorFolio(folio).then((registro) => {
        this.ngxLoader.stop();
        this.router.navigate(['/reune/reclamaciones/editar'], 
          { 
            state: {
              'periodo': this.periodo, 
              'editar': true, 
              'folio': folio,
               'registro': registro
            } 
          });
      });
    }else if(tipo == 3){ 
      this.obtenerAclaracionPorFolio(folio).then((registro) => {
        this.ngxLoader.stop();
        this.router.navigate(['/reune/aclaraciones/editar'], 
          { 
            state: {
              'periodo': this.periodo, 
              'editar': true, 
              'folio': folio,
              'registro': registro
            } 
          });
      });
    }
    
  }

  eliminarRegistro(folio: string, tipo: number): void {
    let registro = ""
    switch (tipo) {
      case 1:
        registro = "consulta"
        break;
      case 2:
        registro = "reclamación"
        break;
      case 3:
        registro = "aclaración"
        break;
      default:
        registro = "consulta"
        break;
    }
    Swal.fire({
      title: 'Eliminar ' + registro,
      text: `¿Seguro que deseas eliminar este registro con número de folio ${folio}?`,
      showCancelButton: true,
      confirmButtonText: 'Si, deseo eliminarlo',
      cancelButtonText: 'No'
    }).then((result) => {
      if (result.value) {
        this.ngxLoader.start();
        this.subscription = this.reuneService.eliminarRegistro(folio, tipo).subscribe({
          next: () => {
            this.obtenerPaginado();
            this.ngxLoader.stop();
            this.toastr.success("Registro eliminado satisfactoriamente", 'Registros');
          },
          error: (error) => {
            this.ngxLoader.stop();
            this.toastr.error(`${error.message}`, 'Ha ocurrido un error...');
          }
        });
      }
    });
  }

  obtenerPaginado(): void {
    if (!this.periodo) {
      this.toastr.info('Debe existir un periodo activo', 'Registros');
      return;
    }

    this.ngxLoader.start();
    this.subscription = this.reuneService.obtenerPaginado(this.periodo.mes, this.offset, this.limit, this.searchText).subscribe({
      next: (response: PagedResponse) => {
        this.registros = response.items;
        this.registros$ = this.filter.valueChanges.pipe(
          startWith(''),
          map((text) => this.search(text)),
        );
        this.total = response.total;
        this.ngxLoader.stop();
      },
      error: (error) => {
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        this.ngxLoader.stop();
      }
    });
  }
  paginationPageChange(): void {
    this.obtenerPaginado();
  }
  obtenerConsultaPorFolio(folio: string): Promise<ConsultaREUNE> {
      return new Promise(async (resolve) => {
        this.reuneService.obtenerConsultaPorFolio(folio).subscribe((consulta: ConsultaREUNE) => {
          resolve(consulta);
        });    
    });
  }
  obtenerAclaracionPorFolio(folio: string): Promise<AclaracionREUNE> {
      return new Promise(async (resolve) => {
        this.reuneService.obtenerAclaracionPorFolio(folio).subscribe((aclaracion: AclaracionREUNE) => {
          resolve(aclaracion);
        });    
    });
  }
  obtenerReclamacionPorFolio(folio: string): Promise<ReclamacionREUNE> {
    return new Promise(async (resolve) => {
      this.reuneService.obtenerReclamacionPorFolio(folio).subscribe((reclamacion: ReclamacionREUNE) => {
        resolve(reclamacion);
      });    
  });
}
}