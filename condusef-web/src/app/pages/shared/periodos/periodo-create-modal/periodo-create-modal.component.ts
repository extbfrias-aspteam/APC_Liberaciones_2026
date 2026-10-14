import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { Periodo } from '../../../../core/models';
import { ngbDateToString_yyyy_mm_dd } from '../../../../app.helpers';

@Component({
  selector: 'app-periodo-create-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgbModule],
  templateUrl: './periodo-create-modal.component.html',
  styleUrl: './periodo-create-modal.component.scss'
})
export class PeriodoCreateModalComponent implements OnInit {
  form: FormGroup;
  anios: number[] = [];

  @Input() paramPeriodo: Periodo | undefined;
   /* 1= mensual, 2= semestral, 3= trimestral */
  @Input() tipo = 1;
 
  public  readonly activeModal = inject(NgbActiveModal);
  private readonly toastr      = inject(ToastrService);

  constructor() {
    // se inicializa el formulario
    if (this.tipo == 3){
      this.form = new FormGroup({
        mes:           new FormControl<number>       (0   , [Validators.required]),
        anio:          new FormControl<number>       (0   , [Validators.required]),
        fecha_inicial: new FormControl<any | null>(null),
        fecha_final:   new FormControl<any | null>(null),
        activo:        new FormControl<boolean>      (false, [Validators.required]),
        idEmpresa:     new FormControl<number | null>(null),
      });
    }else{
      this.form = new FormGroup({
        mes:           new FormControl<number>       (0   , [Validators.required]),
        anio:          new FormControl<number>       (0   , [Validators.required]),
        fecha_inicial: new FormControl<any>          (''  , [Validators.required]),
        fecha_final:   new FormControl<any>          (''  , [Validators.required]),
        activo:        new FormControl<boolean>      (false, [Validators.required]),
        idEmpresa:     new FormControl<number | null>(null),
      });
    }
    
    // si un periodo es pasado como parametro, se asignan sus valores
    if (this.paramPeriodo) {
      this.form.patchValue({
        mes:           this.paramPeriodo.mes,
        anio:          this.paramPeriodo.anio,
        fecha_inicial: this.paramPeriodo.fecha_inicial,
        fecha_final:   this.paramPeriodo.fecha_final,
        activo:        this.paramPeriodo.activo
      });
    }
  }

  get getFechaInicial() {
    return this.form.get('fecha_inicial');
  }

  get getFechaFinal() {
    return this.form.get('fecha_final');
  }

  get getMes() {
    return this.form.get('mes');
  }

  get getAnio() {
    return this.form.get('anio');
  }

  ngOnInit(): void {
    this.inicializarControles();
  }

  inicializarControles(): void {
    // se asignan los valores iniciales y se deshabilitan algunos 
    // controles
    let currentDate = new Date();
    let currentYear = currentDate.getFullYear();

    this.form.patchValue({
      mes:  currentDate.getMonth() + 1,
      anio: currentYear,
      fecha_inicial: {
        year:  currentYear,
        month: currentDate.getMonth() + 1,
        day:   1
      },
      fecha_final: {
        year:  currentYear,
        month: currentDate.getMonth() + 1,
        day:   currentDate.getDate()
      }
    });

    if(this.tipo == 3){
      this.anios = [
        currentYear - 2,
        currentYear - 1,
        currentYear,
        currentYear + 1
      ];
    }
  }

  cerrarModal(): void {
    // se generan las fechas en formato Date y se valida que la fecha inicial 
    // no sea mayor a la final
    const selectedYear: number = this.getAnio?.value;

    if(this.tipo == 3){
      switch (this.getMes?.value) {
        case 1:
           // 1 de enero al 31 de marzo
          this.form.patchValue({
            fecha_inicial: {
              year:  selectedYear,
              month: 1,
              day:   1
            },
            fecha_final: {
              year:  selectedYear,
              month: 3,
              day:   31
            }
          });
          break;
        case 2:
          // 1 de abril al  30 de junio
          this.form.patchValue({
            fecha_inicial: {
              year:  selectedYear,
              month: 4,
              day:   1
            },
            fecha_final: {
              year:  selectedYear,
              month: 6,
              day:   30
            }
          });
          break;
        case 3:
          // 1 de julio al 30 de septiembre
          this.form.patchValue({
            fecha_inicial: {
              year:  selectedYear,
              month: 7,
              day:   1
            },
            fecha_final: {
              year:  selectedYear,
              month: 9,
              day:   30
            }
          });
          break;
        case 4:
          // 1 de octubre al 31 de diciembre
          this.form.patchValue({
            fecha_inicial: {
              year:  selectedYear,
              month: 10,
              day:   1
            },
            fecha_final: {
              year:  selectedYear,
              month: 12,
              day:   31
            }
          });
          break;
        default:
           this.toastr.error('Periodo no válido',
            'Ha ocurrido un error...');
      }
    }else{
      const feInicial = new Date(this.getFechaInicial?.value.year, this.getFechaInicial?.value.month - 1, this.getFechaInicial?.value.day);
      const feFinal = new Date(this.getFechaFinal?.value.year  , this.getFechaFinal?.value.month   - 1, this.getFechaFinal?.value.day);

      if (feInicial > feFinal) {
        this.toastr.error(
          'La fecha inicial del periodo debe ser menor a la fecha final del periodo',
          'Ha ocurrido un error...'
        );

        return;
      }
    }
    

    // por compatibilidad las fechas se convierten en string y se parchan 
    // nuevamente en el formulario
    this.form.patchValue({
      fecha_inicial: ngbDateToString_yyyy_mm_dd(this.getFechaInicial?.value),
      fecha_final:   ngbDateToString_yyyy_mm_dd(this.getFechaFinal?.value)
    });

    // cerramos el formulario y regresamos los valores del form
    this.activeModal.close(
      this.form.getRawValue()
    );
  }
}