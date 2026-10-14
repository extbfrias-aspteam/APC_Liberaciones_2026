import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription, firstValueFrom } from 'rxjs';
import { Causa, EntidadFed, MedioRecepcion, Municipio, NivelAtencion, Periodo, Producto, Colonia, CodigoPostal, ConsultaREUNE, ConsultaREUNEError } from '../../../core/models';
import { CatalogosService, ConsultasService, FoliosService, PeriodosService } from '../../../core/services';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { NgbAlertModule, NgbTooltipModule, NgbDatepickerModule, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { ngbDateStructToDate, ngbDateToString_yyyy_mm_dd, String_dd_yy_yyyyToNgbDate } from '../../../app.helpers';

@Component({
  selector: 'app-consultas-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NgbAlertModule, NgbTooltipModule, NgbDatepickerModule],
  templateUrl: './consultas-create.component.html',
  styleUrl: './consultas-create.component.scss'
})
export class ConsultasReuneCreateComponent implements OnInit {
  form: FormGroup;
  mediosRecepcion: MedioRecepcion[];
  nivelesAtencion: NivelAtencion[];
  productos: Producto[];
  causas: Causa[];
  entidadesFed: EntidadFed[];
  codigosPostales: CodigoPostal[];
  colonias: Colonia[];
  localidades: Colonia[];
  municipios: Municipio[];
  origen: string;
  generarFolio: boolean;
  errores: string[];
  erroresMapeados: ConsultaREUNEError[];
  editar: boolean;
  periodo?: Periodo;
  registro?: ConsultaREUNE;
  errorMapping: { [key: string]: keyof ConsultaREUNE };
  placement = 'bottom';
  inicio_periodo!: NgbDateStruct;
  fin_periodo!: NgbDateStruct;

  private subscription?: Subscription;

  private readonly catalogosService = inject(CatalogosService);
  private readonly consultasService = inject(ConsultasService);
  private readonly folioService = inject(FoliosService);
  private readonly toastr = inject(ToastrService);
  private readonly periodoService = inject(PeriodosService);

  private ngxLoader = inject(NgxUiLoaderService);
  constructor() {
    this.mediosRecepcion = [];
    this.nivelesAtencion = [];
    this.productos       = [];
    this.causas          = [];
    this.entidadesFed    = [];
    this.codigosPostales = [];
    this.colonias        = [];
    this.localidades     = [];
    this.municipios      = [];
    this.origen          = '';
    this.editar          = false;
    this.generarFolio    = true;
    this.errores         = [];
    this.erroresMapeados = [];
    this.errorMapping = {
      "Fecha de Recepción": "ConsultasFecRecepcion",
      "trimestre": "ConsultasTrim",
      "nivel de atención": "ConsultascatnivelatenId",
      "Folio duplicado":  "ConsultasFolio",
      "Trimestre": "ConsultasTrim",
      "Estado de concluido o pendiente": "ConsultasEstatusCon",
      "Fecha de Recepción de la consulta": "ConsultasFecRecepcion",
      "Entidad Federativa": "EstadosId",
      "Medio de recepción o canal": "MediosId",
      "Producto y/o servicio no es válido": "Producto",
      "Producto y la Causa no tienen relación": "Producto",
      "Código Postal es opcional": "ConsultasCP",
      "Municipio o Alcaldía": "ConsultasMpioId",
      "Localidad es opcional": "ConsultasLocId",
      "Colonia es opcional": "ConsultasColId",
      "PORI": "ConsultasPori"
    }

    this.form = new FormGroup({
      Id:            new FormControl<number | null>(null),
      Sucursal:          new FormControl<string | null>(null, [Validators.required]),
      ConsultasTrim:            new FormControl<number | null>(null),
      NumConsultas:          new FormControl<number | null>(null),
      ConsultasFolio:          new FormControl<string | null>(null, [Validators.required]),
      ConsultasFecRecepcion:    new FormControl<string | null>(null, [Validators.required]),
      MediosId:  new FormControl<number | null>(null, [Validators.required]),
      ConsultascatnivelatenId:   new FormControl<number | null>(null, [Validators.required]),
      Producto:        new FormControl<string | null>(null, [Validators.required]),
      CausaId:           new FormControl<number | null>(null, [Validators.required]),
      ConsultasPori:              new FormControl<string | null>(null, [Validators.required]),
      ConsultasEstatusCon:           new FormControl<number | null>(null, [Validators.required]),
      EstadosId:      new FormControl<number | null>(null, [Validators.required]),
      ConsultasMpioId:       new FormControl<number | null>(null, [Validators.required]),
      ConsultasLocId:       new FormControl<number | null>(null),
      ConsultasColId:         new FormControl<number | null>(null, [Validators.required]),
      ConsultasFecAten:   new FormControl<string | null>(null),
      ConsultasCP:   new FormControl<string | null>(null),
    });
  }

  /*
    Propiedades
  */
  get getId() {
    return this.form.get('Id');
  }
  get getConsultasFolio() {
    return this.form.get('ConsultasFolio');
  }

  get getProducto() {
    return this.form.get('Producto');
  }

  get getCausa() {
    return this.form.get('CausaId');
  }

  get getEntidadFed() {
    return this.form.get('EstadosId');
  }

  get getMunicipio() {
    return this.form.get('ConsultasMpioId');
  }

  get getConsultasCP() {
    return this.form.get('ConsultasCP');
  }

  get getColonia() {
    return this.form.get('ConsultasColId');
  }

  get getConsultasEstatusCon() {
    return this.form.get('ConsultasEstatusCon');
  }

  get getConsultasFecAten() {
    return this.form.get('ConsultasFecAten');
  }

  get getConsultasFecRecepcion() {
    return this.form.get('ConsultasFecRecepcion');
  }

  async ngOnInit(): Promise<void> {
    this.periodo = history.state.periodo;
    this.editar  = history.state.editar;
    this.registro  = history.state.registro;
    this.errores  = history.state.errores;
    if(this.errores && this.errores.length > 0){
      this.erroresMapeados = this.mapErrorsToFields(this.errores);
    }
    this.obtenerMediosRecepcion();
    this.obtenerNivelesAtencion();
    this.obtenerEntidadesFed();
    this.obtenerProductos();
    this.obtenerPeriodoActivo();

    if (this.editar) {
      await this.inicializarEdicion();
      this.estadoChange();
      this.coloniaChange();
      this.obtenerCausas();
    } else {
      this.inicializarCaptura();
    }
  }

  ngOnDestroy(): void {
      this.subscription?.unsubscribe();
  }

  /*
    Métodos
  */
  async inicializarCaptura(): Promise<void> {
    const nuevoFolio = await this.folioService.siguienteFolio('reune');
    this.form.patchValue({
      ConsultasTrim: this.periodo?.mes,
      NumConsultas: 1,
      ConsultasFolio: nuevoFolio,
      ConsultasFecRecepcion: formatDate(new Date(), "yyyy-MM-dd", "en"),
      numPenalizacion: 0,
    });

    this.form.controls['ConsultasFecAten'].disable();
  }

  async inicializarEdicion(): Promise<void> {
    this.generarFolio = !this.registro?.Reportado;
    if (this.registro!.ConsultasFecRecepcion) {
      let fechaRecString = this.reorderDateString(this.registro!.ConsultasFecRecepcion);
      
      const date = new Date(fechaRecString);
      const ngbDate = {
        year: date.getFullYear(),
        month: date.getMonth() + 1,
        day: date.getDate()
      };   
      
      this.form.patchValue({
        ConsultasFecRecepcion:    ngbDate,
      });
    }
    if (this.registro!.ConsultasFecAten) {
      let fechaResString = this.reorderDateString(this.registro!.ConsultasFecAten);
      
      const date = new Date(fechaResString);
      const ngbDate = {
        year: date.getFullYear(),
        month: date.getMonth() + 1,
        day: date.getDate()
      };  
      
      this.form.patchValue({
        ConsultasFecAten:    ngbDate,
      });
    }
    /*
      Asignando datos con llenado en cascada
    */
    this.form.patchValue({
      EstadosId: this.registro?.EstadosId,
      ConsultasCP:    this.registro?.ConsultasCP,
    });

    await this.obtenerColonias();

    this.form.patchValue({
      ConsultasMpioId:  this.registro?.ConsultasMpioId,
      ConsultasLocId:  this.registro?.ConsultasLocId,
      ConsultasColId:    this.registro?.ConsultasColId,
      Producto:   this.registro?.Producto,
    });
    /*
      Asignando los datos restantes
    */
    this.form.patchValue({
      Id: this.registro?.Id,
      CausaId: this.registro?.CausaId,
      Sucursal: this.registro?.Sucursal,
      ConsultasTrim: this.registro?.ConsultasTrim,
      NumConsultas: this.registro?.NumConsultas,
      ConsultasFolio: this.registro?.ConsultasFolio,
      MediosId: this.registro?.MediosId,
      ConsultascatnivelatenId: this.registro?.ConsultascatnivelatenId,
      ConsultasPori: this.registro?.ConsultasPori,
      ConsultasEstatusCon: this.registro?.ConsultasEstatusCon
    });

    if(this.erroresMapeados.length > 0){
      for (let index = 0; index < this.erroresMapeados.length; index++) {
        if(this.erroresMapeados[index].field){
          console.log(this.erroresMapeados[index].field, this.erroresMapeados[index].message, this.form.controls[this.erroresMapeados[index].field!]);

          this.form.controls[this.erroresMapeados[index].field!].setErrors({'errorCondusef': this.erroresMapeados[index].message});
        }
      }
    }
  }

  obtenerPeriodoActivo(): void {
    this.ngxLoader.start();
    this.periodoService.obtenerPeriodoActivo('reune').then((periodo) => {
      this.periodo = periodo;
      this.form.patchValue({
        ConsultasTrim:     this.periodo?.mes
      });
      this.ngxLoader.stop();
      this.inicio_periodo = String_dd_yy_yyyyToNgbDate(periodo.fecha_inicial);
      this.fin_periodo = String_dd_yy_yyyyToNgbDate(periodo.fecha_final);
    }, (error) => {
      this.ngxLoader.stop();
      this.toastr.error(`${error.error}`, 'Ha ocurrido un error...');
    });
  }

  reorderDateString(date: string): string {
    if (date === "") return "";

    let dateSplit  = date.split("/")
    return `${dateSplit[2]}/${dateSplit[1]}/${dateSplit[0]}`;
  }

  obtenerMediosRecepcion(): void {
      this.subscription = this.catalogosService.obtenerMediosRecepcion('reune').subscribe({
        next: (response: MedioRecepcion[]) => {
          this.mediosRecepcion = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  obtenerNivelesAtencion(): void {
      this.subscription = this.catalogosService.obtenerNivelesAtencion('reune').subscribe({
        next: (response: NivelAtencion[]) => {
          this.nivelesAtencion = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });  
  }

  obtenerProductos(): void {
      this.subscription = this.catalogosService.obtenerProductos('reune').subscribe({
        next: (response: Producto[]) => {
          this.productos = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  obtenerCausas(): void {
      this.subscription = this.catalogosService.obtenerCausas(this.getProducto?.value, 'reune', 1).subscribe({
        next: (response: Causa[]) => {
          this.causas = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  obtenerEntidadesFed(): void {
      this.subscription = this.catalogosService.obtenerEntidadesFed().subscribe({
        next: (response: EntidadFed[]) => {
          this.entidadesFed = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      }); 
  }

  obtenerMunicipios(): void {
      this.subscription = this.catalogosService.obtenerMunicipios(this.getEntidadFed?.value, this.getConsultasCP?.value).subscribe({
        next: (response: Municipio[]) => {
          if(response.length > 1){
            this.municipios = response;
          }
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  async obtenerColonias(): Promise<void> {
    try {
      await firstValueFrom(this.catalogosService.obtenerColonias(this.getConsultasCP?.value))
      .then((response: Colonia[]) => {
        this.colonias = response;
        if (this.colonias.length > 0) {
          this.form.controls['EstadosId'].setValue(this.colonias[0].estadoId);
        }
        this.obtenerMunicipios();
      })
      if (this.colonias.length > 0) {
        this.form.controls['EstadosId'].setValue(this.colonias[0].estadoId);
      }
      this.obtenerMunicipios();
    } catch (error) {
      this.toastr.error(`${error}`, 'Ha ocurrido un error...');
    }
  }

  obtenerCodigosPostales(): void {
      this.subscription = this.catalogosService.obtenerCodigosPostales(this.getEntidadFed?.value).subscribe({
        next: (response: CodigoPostal[]) => {
          this.codigosPostales = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  async nuevoConsultasFolio(): Promise<void> {
      Swal.fire({
        title: 'Generar Folio',
        text: '¿Seguro que deseas generar un nuevo número de folio?',
        showCancelButton: true,
        confirmButtonText: 'Si, deseo generarlo',
        cancelButtonText: 'No'
      }).then(async (result) => {
        if (result.value) {
          // desechamos el folio actual y se genera un nuevo folio
          this.folioService.desecharFolio('reune');
          const nuevoFolio = await this.folioService.siguienteFolio('reune');
          // se asigna el nuevo folio al control
          this.form.patchValue({
            ConsultasFolio: nuevoFolio,
          });
        };
      });
  }

  async limpiarCaptura(): Promise<void> {
    // desechamos el folio actual y se genera un nuevo folio
    this.folioService.desecharFolio('reune');
    const nuevoFolio = await this.folioService.siguienteFolio('reune');
    // se asignan los valores en los controles
    this.form.patchValue({
      Id:          null,
      Sucursal:          null,
      ConsultasTrim:     this.periodo?.mes,
      NumConsultas:      1,
      ConsultasFolio:    nuevoFolio,
      ConsultasFecRecepcion: formatDate(new Date(), "yyyy-MM-dd", "en"),
      MediosId:  null,
      ConsultascatnivelatenId:   null,
      Producto:           null,
      CausaId:            null,
      ConsultasPori:      null,
      ConsultasEstatusCon:null,
      EstadosId:          null,
      ConsultasMpioId:    null,
      ConsultasLocId:     null,
      ConsultasColId:     null,
      ConsultasCP:        null,
      ConsultasFecAten:   null,
    });

    this.form.markAsUntouched();
    this.form.markAsPristine();
  }

  guardarRegistro(): void {
    if (this.getConsultasFecAten?.value) {
      if (ngbDateStructToDate(this.getConsultasFecAten?.value).getTime() < ngbDateStructToDate(this.getConsultasFecRecepcion?.value).getTime()) {
        this.toastr.error(`La fecha de resolución debe ser mayor o igual a la fecha de recepción`, 'Fecha de resolución');
        return;
      }
    }
    console.log(this.getConsultasFecAten?.value)
    this.ngxLoader.start();
    this.consultasService.crearConsulta(JSON.stringify(this.form.value, function replacer(key, value) {
      if (key === "ConsultasFecRecepcion") return ngbDateToString_yyyy_mm_dd(value) 
      if (key === "ConsultasFecAten") return ngbDateToString_yyyy_mm_dd(value)
        return value
    })).subscribe({
      next: () => {
        this.limpiarCaptura();
        this.ngxLoader.stop();
        this.toastr.success('Registro guardado satisfactoriamente', 'Quejas');
      },
      error: (error)=> {
        this.ngxLoader.stop();
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      }
    });
  }

  editarRegistro(): void {
    if (this.getConsultasFecAten?.value) {
      if (ngbDateStructToDate(this.getConsultasFecAten?.value).getTime() < ngbDateStructToDate(this.getConsultasFecRecepcion?.value).getTime()) {
        this.toastr.error(`La fecha de resolución debe ser mayor o igual a la fecha de recepción`, 'Fecha de resolución');
        return;
      }
    }

    this.ngxLoader.start();
    this.consultasService.modificarConsulta(JSON.stringify(this.form.value, function replacer(key, value) {
      if (key === "ConsultasFecRecepcion"){ console.log("value", value); return ngbDateToString_yyyy_mm_dd(value)} 
      if (key === "ConsultasFecAten") return ngbDateToString_yyyy_mm_dd(value) 
      return value
    })).subscribe({
      next: () => {
        this.ngxLoader.stop();
        this.erroresMapeados = [];
        this.errores = [];
        this.toastr.success('Registro editado satisfactoriamente', 'Quejas');
      },
      error: (error)=> {
        this.ngxLoader.stop();
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      }
    });
  }

  /*
    Eventos OnChange
  */
  showCode: boolean = true; // Controla la visibilidad del <code>
  estadoChange(): void {
    // si estado es concluido
    if (this.getConsultasEstatusCon?.value == 2) {
      this.form.controls['ConsultasFecAten'].enable();
      this.form.controls['ConsultasFecAten'].setValidators(Validators.required);
      this.form.controls['ConsultascatnivelatenId'].enable();
      this.form.controls['ConsultascatnivelatenId'].setValidators(Validators.required);
      this.showCode = !this.showCode;
    } else {
      this.form.patchValue({
        ConsultasFecAten: null,
        ConsultascatnivelatenId: null
      });
      this.form.controls['ConsultasFecAten'].disable();
      this.form.controls['ConsultasFecAten'].clearValidators();
      this.form.controls['ConsultascatnivelatenId'].disable();
      this.form.controls['ConsultascatnivelatenId'].clearValidators();
      this.showCode = !this.showCode;
    }
    this.form.controls['ConsultasFecAten'].updateValueAndValidity();
    this.form.controls['ConsultascatnivelatenId'].updateValueAndValidity();
  }

  codigoPostalChange(): void {
    this.obtenerColonias();
  }

  entidadFedChange(): void {
    this.obtenerCodigosPostales();
  }

  coloniaChange(): void {
    var colSeleccionada = this.colonias.find((col)=>{
      return col.coloniaId == this.getColonia?.value
    });
    if (colSeleccionada) {
      this.localidades = [];
      this.localidades.push(colSeleccionada);
      this.municipios = [{
        "estadoId": colSeleccionada.estadoId,
        "municipioId": colSeleccionada.municipioId,
        "municipio": colSeleccionada.municipio
      }];
      this.form.patchValue({
        ConsultasLocId: colSeleccionada?.tipoLocalidadId,
        ConsultasMpioId: colSeleccionada?.municipioId
      });
    }
  }
  mapErrorsToFields(errors: string[]): ConsultaREUNEError[] {
    return errors.map(error => {
      for (const [key, field] of Object.entries(this.errorMapping)) {
        if (error.includes(key)) {
          return { field, message: error };
        }
      }
      return { field: null, message: error };
    });
  }
  
}