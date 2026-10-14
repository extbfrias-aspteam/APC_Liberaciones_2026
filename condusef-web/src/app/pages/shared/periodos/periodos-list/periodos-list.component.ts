import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { Periodo } from '../../../../core/models';
import { PeriodosService } from '../../../../core/services';
import { PeriodoCreateModalComponent } from '../..';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-periodos-list',
  standalone: true,
  imports: [CommonModule, NgbTooltipModule],
  templateUrl: './periodos-list.component.html',
  styleUrl: './periodos-list.component.scss'
})
export class PeriodosListComponent implements OnInit, OnDestroy {
  periodos: Periodo[] = [];
  schema: string | null;
   /* 1= mensual, 2= semestral, 3= trimestral */
  tipo = 1;

  private subscription?: Subscription;

  private toastr         = inject(ToastrService);
  private ngxLoader      = inject(NgxUiLoaderService);
  private periodoService = inject(PeriodosService);
  private modalService   = inject(NgbModal);
  private route          = inject(ActivatedRoute);

  constructor() {
    this.schema = this.route.snapshot.paramMap.get('schema');
    if (this.schema == "reune"){
      this.tipo = 3;
    }
  }

  ngOnInit(): void {
    this.obtenerPeriodos();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  obtenerPeriodos(): void {
    this.ngxLoader.start();
    this.subscription = this.periodoService.obtenerPeriodos(this.schema!).subscribe({
      next: (response: Periodo[]) => {
      this.periodos = response;
      this.ngxLoader.stop();
      }, 
      error: (error) => {
        this.ngxLoader.stop();
        this.toastr.error(`${error.error}`, 'Ha ocurrido un error...');
      }
    });
  }

  crearPeriodo(): void {
    console.log(this.schema);

    const modalRef = this.modalService.open(PeriodoCreateModalComponent, { backdrop: 'static', size: 'md' });
    modalRef.componentInstance.tipo = this.tipo;
    modalRef.result.then((formData: any) => {
      console.log(formData);
      this.subscription = this.periodoService.crearPeriodo(this.schema!, formData).subscribe({
        next: () => {
          this.toastr.success('Periodo creado satisfactoriamente', 'Periodos');
          this.obtenerPeriodos();
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
    }, () => { });
  }
  
  cerrarPeriodo(periodo: Periodo): void {
    Swal.fire({
      title: 'Cerrar periodo',
      text: '¿Seguro que deseas cerrar el periodo seleccionado?',
      showCancelButton: true,
      confirmButtonText: 'Si, deseo cerrarlo',
      cancelButtonText: 'No'
    }).then((result) => {
      if (result.value) {
        this.ngxLoader.start();
        this.subscription = this.periodoService.cerrarPeriodoActivo(this.schema!, periodo.mes, periodo.anio).subscribe({
          next: () => {
            this.ngxLoader.stop();
            this.toastr.success('Periodo cerrado satisfactoriamente', 'Periodos');
            this.obtenerPeriodos();
          },
          error: (error) => {
            this.ngxLoader.stop();
            this.toastr.error(`${error}`, 'Ha ocurrido un error...');
          }
        });
      }
    });
  }

  abrirPeriodo(periodo: Periodo): void {
    Swal.fire({
      title: 'Abrir periodo',
      text: '¿Seguro que deseas abrir el periodo seleccionado?',
      showCancelButton: true,
      confirmButtonText: 'Si, deseo abrirlo',
      cancelButtonText: 'No'
    }).then((result) => {
      if (result.value) {
        this.ngxLoader.start();
        this.subscription = this.periodoService.abrirPeriodo(this.schema!, periodo.mes, periodo.anio).subscribe({
          next: () => {
            this.ngxLoader.stop();
            this.toastr.success('Periodo abierto satisfactoriamente', 'Periodos');
            this.obtenerPeriodos();
          },
          error: (error) => {
            this.ngxLoader.stop();
            this.toastr.error(`${error}`, 'Ha ocurrido un error...');
          }
        });
      }
    });
  }
}
