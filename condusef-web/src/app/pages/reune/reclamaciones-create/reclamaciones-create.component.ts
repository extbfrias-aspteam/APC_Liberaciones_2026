

import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Subscription, firstValueFrom } from 'rxjs';
import {
  Causa,
  EntidadFed,
  MedioRecepcion,
  Municipio,
  NivelAtencion,
  Periodo,
  Producto,
  Colonia,
  CodigoPostal,
  ReclamacionREUNE,
  ReclamacionREUNEError,
} from '../../../core/models';
import {
  CatalogosService,
  ReclamacionesService,
  FoliosService,
  PeriodosService,
} from '../../../core/services';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import {
  NgbAlertModule,
  NgbTooltipModule,
  NgbDatepickerModule,
  NgbDateStruct,
} from '@ng-bootstrap/ng-bootstrap';
import {
  ngbDateStructToDate,
  ngbDateToString_yyyy_mm_dd,
  String_dd_yy_yyyyToNgbDate,
} from '../../../app.helpers';

@Component({
  selector: 'app-reclamaciones-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    NgbAlertModule,
    NgbTooltipModule,
    NgbDatepickerModule,
  ],
  templateUrl: './reclamaciones-create.component.html',
  styleUrl: './reclamaciones-create.component.scss',
})
export class ReclamacionesReuneCreateComponent implements OnInit {
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
  erroresMapeados: ReclamacionREUNEError[];
  errorMapping: { [key: string]: keyof ReclamacionREUNE };
  placement = 'bottom';
  inicio_periodo!: NgbDateStruct;
  fin_periodo!: NgbDateStruct;
  model1!: NgbDateStruct;
  model2!: NgbDateStruct;
  model3!: NgbDateStruct;
  model4!: NgbDateStruct;
  model5!: NgbDateStruct;

  editar: boolean;
  periodo?: Periodo;
  registro?: ReclamacionREUNE;

  private subscription?: Subscription;

  private readonly catalogosService = inject(CatalogosService);
  private readonly reclamacionesService = inject(ReclamacionesService);
  private readonly folioService = inject(FoliosService);
  private readonly toastr = inject(ToastrService);
  private readonly periodoService = inject(PeriodosService);

  private ngxLoader = inject(NgxUiLoaderService);

  constructor() {
    this.mediosRecepcion = [];
    this.nivelesAtencion = [];
    this.productos = [];
    this.causas = [];
    this.entidadesFed = [];
    this.codigosPostales = [];
    this.colonias = [];
    this.localidades = [];
    this.municipios = [];
    this.origen = '';
    this.editar = false;
    this.generarFolio = true;
    this.errores = [];
    this.erroresMapeados = [];
    this.errorMapping = {
      'Fecha de Reclamacion': 'RecFechaReclamacion',
      'Fecha de Atención': 'RecFechaAtencion',
      'RecProductoServicio y la Causa no tienen relación':
        'RecProductoServicio',
      'Fecha de Resolución': 'RecFechaResolucion',
      'Fecha de Notificación al usuario': 'RecFechaNotifiUsuario',
      'Código Postal': 'RecCodigoPostal',
      Municipio: 'RecMunicipioAlcaldia',
      Localidad: 'RecLocalidad',
      Colonia: 'RecColonia',
      'Monto reclamado': 'RecMontoReclamado',
      'Importe o cantidad abonada al usuario': 'RecImporteAbonado',
      RecSexo: 'RecSexo',
      RecEdad: 'RecEdad',
      'Sentido de la resolución': 'RecSentidoResolucion',
      'Nivel de atención': 'RecNivelAtencion',
      'Folio asignado por CONDUSEF': 'RecFolioCondusef',
      ReversaSIGE: 'RecReversa',
    };

    this.form = new FormGroup({
      Id: new FormControl<number | null>(null),
      RecTrimestre: new FormControl<number | null>(null, [Validators.required]),
      RecNumero: new FormControl<number | null>(null, [Validators.required]),
      RecFolioAtencion: new FormControl<string | null>(null, [
        Validators.required,
      ]),
      RecEstadoConPend: new FormControl<number | null>(null, [
        Validators.required,
      ]),
      RecFechaReclamacion: new FormControl<string | null>(null, [
        Validators.required,
      ]),
      RecFechaAtencion: new FormControl<string | null>(null),
      RecMedioRecepcionCanal: new FormControl<number | null>(null, [
        Validators.required,
      ]),
      RecProductoServicio: new FormControl<string | null>(null, [
        Validators.required,
      ]),
      RecCausaMotivo: new FormControl<string | null>(null, [
        Validators.required,
      ]),
      RecFechaResolucion: new FormControl<string | null>(null),
      RecFechaNotifiUsuario: new FormControl<string | null>(null),
      RecFechaAbonoImporte: new FormControl<string | null>(null),
      RecEntidadFederativa: new FormControl<number | null>(null, [
        Validators.required,
      ]),
      RecCodigoPostal: new FormControl<number | null>(null),
      RecMunicipioAlcaldia: new FormControl<number | null>(null, [
        Validators.required,
      ]),
      RecLocalidad: new FormControl<number | null>(null),
      RecColonia: new FormControl<number | null>(null),
      RecMonetario: new FormControl<string | null>(null, [Validators.required]),
      RecMontoReclamado: new FormControl<number | null>(null),
      RecImporteAbonado: new FormControl<number | null>(null),
      RecPori: new FormControl<string | null>(null, [Validators.required]),
      RecTipoPersona: new FormControl<number | null>(null, [
        Validators.required,
      ]),
      RecSexo: new FormControl<string | null>(null),
      RecEdad: new FormControl<number | null>(null),
      RecNivelAtencion: new FormControl<number | null>(null),
      RecFolioCondusef: new FormControl<string | null>(null),
      RecReversa: new FormControl<number | null>(null),
      //RecOperacionExtranjero: new FormControl<string | null>(null, [Validators.required]),
      RecSentidoResolucion: new FormControl<number | null>(null),
      Sucursal: new FormControl<number | null>(null),
      Estatus: new FormControl<number | null>(null),
    });
  }

  /*
    Propiedades
  */
  get getId() {
    return this.form.get('Id');
  }
  get getRecDenominacion() {
    return this.form.get('RecDenominacion');
  }

  get getRecSector() {
    return this.form.get('RecSector');
  }

  get getRecTrimestre() {
    return this.form.get('RecTrimestre');
  }

  get getRecNumero() {
    return this.form.get('RecNumero');
  }

  get getRecFolioAtencion() {
    return this.form.get('RecFolioAtencion');
  }

  get getRecEstadoConPend() {
    return this.form.get('RecEstadoConPend');
  }

  get getRecFechaReclamacion() {
    return this.form.get('RecFechaReclamacion');
  }

  get getRecFechaAtencion() {
    return this.form.get('RecFechaAtencion');
  }

  get getRecMedioRecepcionCanal() {
    return this.form.get('RecMedioRecepcionCanal');
  }

  get getRecProductoServicio() {
    return this.form.get('RecProductoServicio');
  }

  get getRecCausaMotivo() {
    return this.form.get('RecCausaMotivo');
  }

  get getRecFechaResolucion() {
    return this.form.get('RecFechaResolucion');
  }

  get getRecFechaNotifiUsuario() {
    return this.form.get('RecFechaNotifiUsuario');
  }

  get getRecEntidadFederativa() {
    return this.form.get('RecEntidadFederativa');
  }

  get getRecCodigoPostal() {
    return this.form.get('RecCodigoPostal');
  }

  get getRecMunicipioAlcaldia() {
    return this.form.get('RecMunicipioAlcaldia');
  }

  get getRecLocalidad() {
    return this.form.get('RecLocalidad');
  }

  get getRecColonia() {
    return this.form.get('RecColonia');
  }

  get getRecMonetario() {
    return this.form.get('RecMonetario');
  }

  get getRecImporteAbonado() {
    return this.form.get('RecImporteAbonado');
  }

  get getRecMontoReclamado() {
    return this.form.get('RecMontoReclamado');
  }

  get getRecPori() {
    return this.form.get('RecPori');
  }

  get getRecTipoPersona() {
    return this.form.get('RecTipoPersona');
  }

  get getRecSexo() {
    return this.form.get('RecSexo');
  }

  get getRecEdad() {
    return this.form.get('RecEdad');
  }

  get getRecNivelAtencion() {
    return this.form.get('RecNivelAtencion');
  }

  get getRecFolioCondusef() {
    return this.form.get('RecFolioCondusef');
  }

  get getRecReversa() {
    return this.form.get('RecReversa');
  }

  // get getRecOperacionExtranjero() {
  //   return this.form.get('RecOperacionExtranjero');
  // }

  get getSucursal() {
    return this.form.get('Sucursal');
  }

  get getEstatus() {
    return this.form.get('Estatus');
  }
  get getRecSentidoResolucion() {
    return this.form.get('RecSentidoResolucion');
  }

  async ngOnInit(): Promise<void> {
    this.periodo = history.state.periodo;
    this.editar = history.state.editar;
    this.registro = history.state.registro;
    this.errores = history.state.errores;
    if (this.errores && this.errores.length > 0) {
      this.erroresMapeados = this.mapErrorsToFields(this.errores);
    }

    this.obtenerMediosRecepcion();
    this.obtenerNivelesAtencion();
    this.obtenerEntidadesFed();
    this.obtenerReclamacionProductoServicios();
    this.obtenerPeriodoActivo();

    if (this.editar) {
      await this.inicializarEdicion();
      this.estadoChange();
      this.coloniaChange();
      this.obtenerCausas();
      this.obtenerMunicipios();
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
      RecTrimestre: this.periodo?.mes,
      RecNumero: 1,
      RecFolioAtencion: nuevoFolio,
      RecFechaReclamacion: formatDate(new Date(), 'yyyy-MM-dd', 'en'),
      numPenalizacion: 0,
    });

    this.form.controls['RecFechaAtencion'].disable();
    this.form.controls['RecFechaNotifiUsuario'].disable();
    this.form.controls['RecFechaResolucion'].disable();
    this.form.controls['RecReversa'].disable();
    this.form.controls['RecFolioCondusef'].disable();
    this.form.controls['RecNivelAtencion'].disable();
    this.form.controls['RecMontoReclamado'].disable();
    this.form.controls['RecImporteAbonado'].disable();
    this.form.controls['RecFechaAbonoImporte'].disable();
    this.form.controls['RecSentidoResolucion'].disable();
  }

  async inicializarEdicion(): Promise<void> {
    this.generarFolio = !this.registro?.Reportado;
    /*
      Asignando fechas al formulario de edición

      Las fechas vienen en formato dd/MM/yyyy y con la función reorderDateString se transforman
      a formato yyyy/MM/dd para su correcta asignacion usando new Date()
    */
    if (this.registro!.RecMedioRecepcionCanal != 6) {
      this.form.get('RecReversa')?.disable();
      //this.form.get('RecFolioCondusef')?.disable();
    }

    if (this.registro!.RecTipoPersona == 1) {
      this.form.get('RecSexo')?.enable();
      this.form.get('RecEdad')?.enable();
    }

    if (this.registro!.RecMonetario == 'NO') {
      this.form.get('RecImporteAbonado')?.disable();
      this.form.get('RecMontoReclamado')?.disable();
    }

    if (this.registro!.RecFechaReclamacion) {
      this.model1 = String_dd_yy_yyyyToNgbDate(
        this.registro!.RecFechaReclamacion
      );
      let fechaResString = this.reorderDateString(
        this.registro!.RecFechaReclamacion
      );
      this.form.patchValue({
        RecFechaReclamacion: formatDate(
          new Date(fechaResString),
          'yyyy-MM-dd',
          'en'
        ),
      });
    }
    if (this.registro!.RecFechaAtencion) {
      this.model2 = String_dd_yy_yyyyToNgbDate(this.registro!.RecFechaAtencion);
      let fechaResString = this.reorderDateString(
        this.registro!.RecFechaAtencion
      );
      this.form.patchValue({
        RecFechaAtencion: formatDate(
          new Date(fechaResString),
          'yyyy-MM-dd',
          'en'
        ),
      });
    }
    if (this.registro!.RecFechaAbonoImporte) {
      this.model3 = String_dd_yy_yyyyToNgbDate(
        this.registro!.RecFechaAbonoImporte
      );
      let fechaResString = this.reorderDateString(
        this.registro!.RecFechaAbonoImporte
      );
      this.form.patchValue({
        RecFechaAbonoImporte: formatDate(
          new Date(fechaResString),
          'yyyy-MM-dd',
          'en'
        ),
      });
    }
    if (this.registro!.RecFechaNotifiUsuario) {
      this.model4 = String_dd_yy_yyyyToNgbDate(
        this.registro!.RecFechaNotifiUsuario
      );
      let fechaResString = this.reorderDateString(
        this.registro!.RecFechaNotifiUsuario
      );
      this.form.patchValue({
        RecFechaNotifiUsuario: formatDate(
          new Date(fechaResString),
          'yyyy-MM-dd',
          'en'
        ),
      });
    }
    if (this.registro!.RecFechaResolucion) {
      this.model5 = String_dd_yy_yyyyToNgbDate(
        this.registro!.RecFechaResolucion
      );
      let fechaResString = this.reorderDateString(
        this.registro!.RecFechaResolucion
      );
      this.form.patchValue({
        RecFechaResolucion: formatDate(
          new Date(fechaResString),
          'yyyy-MM-dd',
          'en'
        ),
      });
    }

    /*
      Asignando datos con llenado en cascada
    */
    this.form.patchValue({
      RecEntidadFederativa: this.registro?.RecEntidadFederativa,
      RecCodigoPostal: this.registro?.RecCodigoPostal,
    });

    if (this.registro?.RecCodigoPostal != null) {
      await this.obtenerColonias();
    } else {
      this.form.controls['RecCodigoPostal'].disable();
      this.form.controls['RecLocalidad'].disable();
      this.form.controls['RecColonia'].disable();
      this.form.controls['RecCodigoPostal'].clearValidators();
      this.form.controls['RecLocalidad'].clearValidators();
      this.form.controls['RecColonia'].clearValidators();
    }
    this.form.patchValue({
      Id: this.registro?.ID,
      RecMunicipioAlcaldia: this.registro?.RecMunicipioAlcaldia,
      RecLocalidad: this.registro?.RecLocalidad,
      RecColonia: this.registro?.RecColonia,
      RecProductoServicio: this.registro?.RecProductoServicio,
      RecCausaMotivo: this.registro?.RecCausaMotivo,
      Sucursal: this.registro?.Sucursal,
      RecTrimestre: this.registro?.RecTrimestre,
      RecNumero: this.registro?.RecNumero,
      RecFolioAtencion: this.registro?.RecFolioAtencion,
      RecMedioRecepcionCanal: this.registro?.RecMedioRecepcionCanal,
      RecNivelAtencion: this.registro?.RecNivelAtencion,
      RecPori: this.registro?.RecPori,
      RecEstadoConPend: this.registro?.RecEstadoConPend,
      RecTipoPersona: this.registro?.RecTipoPersona,
      RecSexo: this.registro?.RecSexo,
      RecMonetario: this.registro?.RecMonetario,
      RecMontoReclamado: this.registro?.RecMontoReclamado,
      RecEdad: this.registro?.RecEdad,
      RecImporteAbonado: this.registro?.RecImporteAbonado,
      RecFolioCondusef: this.registro?.RecFolioCondusef,
      RecSentidoResolucion: this.registro?.RecSentidoResolucion,
      // RecOperacionExtranjero: this.registro?.RecOperacionExtranjero,
    });
    this.tipoPersonaChange();

    if (this.erroresMapeados.length > 0) {
      for (let index = 0; index < this.erroresMapeados.length; index++) {
        if (this.erroresMapeados[index].field) {
          this.form.controls[this.erroresMapeados[index].field!].setErrors({
            errorCondusef: this.erroresMapeados[index].message,
          });
        }
      }
    }
  }

  obtenerPeriodoActivo(): void {
    this.ngxLoader.start();
    this.periodoService.obtenerPeriodoActivo('reune').then(
      (periodo) => {
        this.periodo = periodo;
        this.form.patchValue({
          RecTrimestre: this.periodo?.mes,
        });
        this.ngxLoader.stop();
        this.inicio_periodo = String_dd_yy_yyyyToNgbDate(periodo.fecha_inicial);
        this.fin_periodo = String_dd_yy_yyyyToNgbDate(periodo.fecha_final);
      },
      (error) => {
        this.ngxLoader.stop();
        this.toastr.error(`${error.error}`, 'Ha ocurrido un error...');
      }
    );
  }

  reorderDateString(date: string): string {
    if (date === '') return '';

    let dateSplit = date.split('/');
    return `${dateSplit[2]}/${dateSplit[1]}/${dateSplit[0]}`;
  }

  obtenerMediosRecepcion(): void {
    this.subscription = this.catalogosService
      .obtenerMediosRecepcion('reune')
      .subscribe({
        next: (response: MedioRecepcion[]) => {
          this.mediosRecepcion = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        },
      });
  }

  obtenerNivelesAtencion(): void {
    this.subscription = this.catalogosService
      .obtenerNivelesAtencion('reune')
      .subscribe({
        next: (response: NivelAtencion[]) => {
          this.nivelesAtencion = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        },
      });
  }

  obtenerReclamacionProductoServicios(): void {
    this.subscription = this.catalogosService
      .obtenerProductos('reune')
      .subscribe({
        next: (response: Producto[]) => {
          this.productos = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        },
      });
  }

  obtenerCausas(): void {
    this.subscription = this.catalogosService
      .obtenerCausas(this.getRecProductoServicio?.value, 'reune', 2)
      .subscribe({
        next: (response: Causa[]) => {
          this.causas = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        },
      });
  }

  obtenerEntidadesFed(): void {
    this.subscription = this.catalogosService.obtenerEntidadesFed().subscribe({
      next: (response: EntidadFed[]) => {
        this.entidadesFed = response;
      },
      error: (error) => {
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      },
    });
  }

  obtenerMunicipios(): void {
    this.subscription = this.catalogosService
      .obtenerMunicipios(
        this.getRecEntidadFederativa?.value,
        this.getRecCodigoPostal?.value
      )
      .subscribe({
        next: (response: Municipio[]) => {
          if (response.length > 1) {
            this.municipios = response;
          }
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        },
      });
  }

  async obtenerColonias(): Promise<void> {
    try {
      await firstValueFrom(
        this.catalogosService.obtenerColonias(this.getRecCodigoPostal?.value)
      ).then((response: Colonia[]) => {
        this.colonias = response;
        if (this.colonias.length > 0) {
          this.form.controls['RecEntidadFederativa'].setValue(
            this.colonias[0].estadoId
          );
        }
        this.obtenerMunicipios();
      });
      if (this.colonias.length > 0) {
        this.form.controls['RecEntidadFederativa'].setValue(
          this.colonias[0].estadoId
        );
      }
      this.obtenerMunicipios();
    } catch (error) {
      this.toastr.error(`${error}`, 'Ha ocurrido un error...');
    }
  }

  obtenerCodigosPostales(): void {
    this.subscription = this.catalogosService
      .obtenerCodigosPostales(this.getRecEntidadFederativa?.value)
      .subscribe({
        next: (response: CodigoPostal[]) => {
          this.codigosPostales = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        },
      });
  }

  async nuevoReclamacionFolioAtencion(): Promise<void> {
    Swal.fire({
      title: 'Generar Folio',
      text: '¿Seguro que deseas generar un nuevo número de folio?',
      showCancelButton: true,
      confirmButtonText: 'Si, deseo generarlo',
      cancelButtonText: 'No',
    }).then(async (result) => {
      if (result.value) {
        // desechamos el folio actual y se genera un nuevo folio
        this.folioService.desecharFolio('reune');
        const nuevoFolio = await this.folioService.siguienteFolio('reune');
        // se asigna el nuevo folio al control
        this.form.patchValue({
          RecFolioAtencion: nuevoFolio,
        });
      }
    });
  }

  async limpiarCaptura(): Promise<void> {
    // desechamos el folio actual y se genera un nuevo folio
    this.folioService.desecharFolio('reune');
    const nuevoFolio = await this.folioService.siguienteFolio('reune');
    // se asignan los valores en los controles
    this.form.patchValue({
      Sucursal: null,
      RecTrimestre: this.periodo?.mes,
      RecNumero: 1,
      RecFolioAtencion: nuevoFolio,
      RecFechaReclamacion: formatDate(new Date(), 'yyyy-MM-dd', 'en'),
      RecMedioRecepcionCanal: null,
      RecNivelAtencion: null,
      RecProductoServicio: null,
      RecCausaMotivo: null,
      RecPori: null,
      RecEstadoConPend: null,
      RecEntidadFederativa: null,
      RecMunicipioAlcaldia: null,
      RecLocalidad: null,
      RecColonia: null,
      RecCodigoPostal: null,
      RecFechaAtencion: null,
      RecFechaResolucion: null,
      RecFechaNotifiUsuario: null,
      RecFechaAbonoImporte: null,
    });

    this.form.markAsUntouched();
    this.form.markAsPristine();
  }

guardarRegistro(): void {
  // Validaciones de fechas
  if (this.getRecFechaAtencion?.value) {
    const fa  = ngbDateStructToDate(this.getRecFechaAtencion?.value).getTime();
    const fr  = ngbDateStructToDate(this.getRecFechaResolucion?.value).getTime();
    const fnu = ngbDateStructToDate(this.getRecFechaNotifiUsuario?.value).getTime();

    if (fa > fr) {
      this.toastr.error('La fecha de resolución debe ser mayor o igual a la fecha de atención','Fecha de resolución');
      return;
    }
    if (fnu < fr) {
      this.toastr.error('La fecha de notificación al usuario debe ser mayor o igual a la fecha de resolución','Fecha de atención a usuario');
      return;
    }
  }

  const v = this.form.value;
  const monetario = String(v?.RecMonetario ?? '').trim().toUpperCase();


  let recImporteAbonado: number | null = null;
  if (monetario !== 'NO') {
    const s = String(v?.RecImporteAbonado ?? '').trim().replace(/,/g, '');
    const n = s === '' ? NaN : Number(s);
    recImporteAbonado = Number.isFinite(n) ? n : null;
  } 

  const payload = {
    ...v,
    RecCausaMotivo: v?.RecCausaMotivo != null ? String(v.RecCausaMotivo) : null,
    RecEdad: (v?.RecEdad !== '' && v?.RecEdad != null) ? parseInt(v.RecEdad, 10) : null,
    RecMonetario: monetario, // ya normalizado
    RecSexo: v?.RecSexo != null ? String(v.RecSexo) : null,
    RecMontoReclamado: (v?.RecMontoReclamado !== '' && v?.RecMontoReclamado != null) ? parseInt(v.RecMontoReclamado, 10) : null,
    RecTipoPersona: (v?.RecTipoPersona !== '' && v?.RecTipoPersona != null) ? parseInt(v.RecTipoPersona, 10) : null,
    RecSentidoResolucion: (v?.RecSentidoResolucion !== '' && v?.RecSentidoResolucion != null) ? parseInt(v.RecSentidoResolucion, 10) : null,
    RecReversa: v?.RecReversa ? 1 : 0,
    RecFechaReclamacion: v?.RecFechaReclamacion ? ngbDateToString_yyyy_mm_dd(v.RecFechaReclamacion) : null,
    RecFechaAtencion: v?.RecFechaAtencion ? ngbDateToString_yyyy_mm_dd(v.RecFechaAtencion) : null,
    RecFechaResolucion: v?.RecFechaResolucion ? ngbDateToString_yyyy_mm_dd(v.RecFechaResolucion) : null,
    RecFechaNotifiUsuario: v?.RecFechaNotifiUsuario ? ngbDateToString_yyyy_mm_dd(v.RecFechaNotifiUsuario) : null,
    RecFechaAbonoImporte: v?.RecFechaAbonoImporte ? ngbDateToString_yyyy_mm_dd(v.RecFechaAbonoImporte) : null,


    RecImporteAbonado: recImporteAbonado,
  };


  this.ngxLoader.start();
  this.reclamacionesService
    .crearReclamacion(JSON.stringify(payload))
    .subscribe({
      next: () => {
        this.limpiarCaptura();
        this.ngxLoader.stop();
        this.toastr.success('Registro guardado satisfactoriamente', 'Quejas');
      },
      error: (error) => {
        this.ngxLoader.stop();
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      },
    });
}

editarRegistro(): void {

  const formData = this.form.getRawValue();

  if (formData.RecFechaAtencion && formData.RecFechaResolucion) {
    if (
      ngbDateStructToDate(formData.RecFechaAtencion).getTime() >
      ngbDateStructToDate(formData.RecFechaResolucion).getTime()
    ) {
      this.toastr.error(
        'La fecha de resolución debe ser mayor o igual a la fecha de atención',
        'Fecha de resolución'
      );
      return;
    }
  }

  const payload = JSON.stringify(formData, (key, value) => {

    if (key === 'RecCausaMotivo') return value?.toString();
    if (key === 'RecSexo') return value?.toString();
    if (key === 'RecEdad') return value ? parseInt(value, 10) : null;

    if (key === 'RecImporteAbonado') {
      if (value === null || value === undefined || value === '') {
        return 0;
      }
      return parseInt(value, 10);
    }

    if (key === 'RecMonetario') return value?.toString();
    if (key === 'RecMontoReclamado') return value ? parseInt(value, 10) : null;
    if (key === 'RecReversa') return value ? 1 : 0;
    if (key === 'RecTipoPersona') return value ? parseInt(value, 10) : null;
    if (key === 'RecSentidoResolucion') return value ? parseInt(value, 10) : null;

    if (key === 'RecFechaReclamacion') return value ? ngbDateToString_yyyy_mm_dd(value) : null;
    if (key === 'RecFechaAtencion') return value ? ngbDateToString_yyyy_mm_dd(value) : null;
    if (key === 'RecFechaResolucion') return value ? ngbDateToString_yyyy_mm_dd(value) : null;
    if (key === 'RecFechaNotifiUsuario') return value ? ngbDateToString_yyyy_mm_dd(value) : null;
    if (key === 'RecFechaAbonoImporte') return value ? ngbDateToString_yyyy_mm_dd(value) : null;

    return value;
  });

  this.ngxLoader.start();

  this.reclamacionesService
    .modificarReclamacion(payload)
    .subscribe({
      next: () => {
        this.ngxLoader.stop();
        this.toastr.success('Registro editado satisfactoriamente', 'Quejas');
      },
      error: (error) => {
        this.ngxLoader.stop();
        this.toastr.warning(
          'Por favor revisa que todos los campos estén completos',
          'Validación'
        );
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      },
    });
}

  /*
    Eventos OnChange
  */
  tipoPersonaChange(): void {
    // si tipo de persona es fisica
    if (this.getRecTipoPersona?.value == 1) {
      this.form.controls['RecSexo'].enable();
      this.form.controls['RecSexo'].setValidators(Validators.required);
      this.form.controls['RecEdad'].enable();
      this.form.controls['RecEdad'].setValidators([
        Validators.required,
        Validators.max(999),
      ]);
    } else {
      this.form.patchValue({
        RecSexo: null,
        RecEdad: null,
      });
      this.form.controls['RecSexo'].disable();
      this.form.controls['RecSexo'].clearValidators();
      this.form.controls['RecEdad'].disable();
      this.form.controls['RecEdad'].clearValidators();
    }
    this.form.controls['RecSexo'].updateValueAndValidity();
    this.form.controls['RecEdad'].updateValueAndValidity();
  }
  showCode: boolean = true; // Controla la visibilidad del <code>
  estadoChange(): void {
    // si estado es concluido
    if (this.getRecEstadoConPend?.value == 2) {
      this.form.controls['RecFechaAtencion'].enable();
      this.form.controls['RecFechaAtencion'].setValidators(Validators.required);
      this.form.controls['RecFechaNotifiUsuario'].enable();
      this.form.controls['RecFechaNotifiUsuario'].setValidators(
        Validators.required
      );
      this.form.controls['RecNivelAtencion'].enable();
      this.form.controls['RecNivelAtencion'].setValidators(Validators.required);
      this.showCode = !this.showCode;
      this.form.controls['RecFechaResolucion'].enable();
      this.form.controls['RecFechaResolucion'].setValidators(
        Validators.required
      );
      this.form.controls['RecSentidoResolucion'].enable();
      this.form.controls['RecSentidoResolucion'].setValidators(
        Validators.required
      );
    } else {
      this.form.patchValue({
        RecFechaAtencion: null,
        RecFechaNotifiUsuario: null,
        RecNivelAtencion: null,
        RecFechaResolucion: null,
        RecSentidoResolucion: null,
      });
      this.form.controls['RecFechaAtencion'].disable();
      this.form.controls['RecFechaAtencion'].clearValidators();
      this.form.controls['RecFechaNotifiUsuario'].disable();
      this.form.controls['RecFechaNotifiUsuario'].clearValidators();
      this.form.controls['RecNivelAtencion'].disable();
      this.form.controls['RecNivelAtencion'].clearValidators();
      this.showCode = !this.showCode;
      this.form.controls['RecFechaResolucion'].disable();
      this.form.controls['RecFechaResolucion'].clearValidators();
      this.form.controls['RecSentidoResolucion'].disable();
      this.form.controls['RecSentidoResolucion'].clearValidators();
    }
    this.form.controls['RecFechaAtencion'].updateValueAndValidity();
    this.form.controls['RecFechaNotifiUsuario'].updateValueAndValidity();
    this.form.controls['RecNivelAtencion'].updateValueAndValidity();
    this.form.controls['RecFechaResolucion'].updateValueAndValidity();
    this.form.controls['RecSentidoResolucion'].updateValueAndValidity();
  }

  medioChange(): void {
    //si el medio de recepción es UNE, Sucursal u Oficina de atención al público
    if (
      this.getRecMedioRecepcionCanal?.value == 5 ||
      this.getRecMedioRecepcionCanal?.value == 3 ||
      this.getRecMedioRecepcionCanal?.value == 17
    ) {
      this.form.controls['RecCodigoPostal'].enable();
      this.form.controls['RecCodigoPostal'].setValidators(Validators.required);
      this.form.controls['RecLocalidad'].enable();
      this.form.controls['RecColonia'].enable();
    } else {
      this.form.patchValue({
        RecCodigoPostal: null,
        RecLocalidad: null,
        RecColonia: null,
      });
      this.form.controls['RecCodigoPostal'].disable();
      this.form.controls['RecLocalidad'].disable();
      this.form.controls['RecColonia'].disable();
      this.form.controls['RecCodigoPostal'].clearValidators();
      this.form.controls['RecLocalidad'].clearValidators();
      this.form.controls['RecColonia'].clearValidators();
    }
    //6 = "CONDUSEF-SIGE gestión electrónica", 7 = "CONDUSEF-Gestión ordinaria"
    if (
      this.getRecMedioRecepcionCanal?.value == 6 ||
      this.getRecMedioRecepcionCanal?.value == 7
    ) {
      if (this.getRecMedioRecepcionCanal?.value == 6) {
        this.form.controls['RecFolioCondusef'].enable();
        this.form.controls['RecFolioCondusef'].setValidators(
          Validators.required
        );
        this.form.controls['RecReversa'].enable();
      } else {
        this.form.controls['RecFolioCondusef'].enable();
        this.form.controls['RecFolioCondusef'].setValidators(
          Validators.required
        );

        this.form.patchValue({
          RecReversa: null,
        });
        this.form.controls['RecReversa'].disable();
      }
    } else {
      this.form.patchValue({
        RecReversa: null,
        RecFolioCondusef: null,
      });
      this.form.controls['RecReversa'].disable();
      this.form.controls['RecFolioCondusef'].disable();
      this.form.controls['RecFolioCondusef'].clearValidators();
    }

    this.form.controls['RecCodigoPostal'].updateValueAndValidity();
    this.form.controls['RecLocalidad'].updateValueAndValidity();
    this.form.controls['RecColonia'].updateValueAndValidity();
    this.form.controls['RecReversa'].updateValueAndValidity();
    this.form.controls['RecFolioCondusef'].updateValueAndValidity();
  }

  codigoPostalChange(): void {
    this.obtenerColonias();
  }

  entidadFedChange(): void {
    this.obtenerCodigosPostales();
    this.obtenerMunicipios();
  }

  coloniaChange(): void {
    var colSeleccionada = this.colonias.find((col) => {
      return col.coloniaId == this.getRecColonia?.value;
    });

    if (colSeleccionada) {
      this.localidades = [];
      this.localidades.push(colSeleccionada);
      this.municipios = [
        {
          estadoId: colSeleccionada.estadoId,
          municipioId: colSeleccionada.municipioId,
          municipio: colSeleccionada.municipio,
        },
      ];
      this.form.patchValue({
        RecLocalidad: colSeleccionada?.tipoLocalidadId,
        RecMunicipioAlcaldia: colSeleccionada?.municipioId,
      });
    }
  }

  reclamacionMonetarioChange(): void {
    // si tipo de persona es fisica
    if (this.getRecMonetario?.value == 'SI') {
      this.form.controls['RecMontoReclamado'].enable();
      this.form.controls['RecMontoReclamado'].setValidators(
        Validators.required
      );
      this.form.controls['RecImporteAbonado'].enable();
      this.form.controls['RecImporteAbonado'].setValidators(
        Validators.required
      );
    } else {
      this.form.patchValue({
        RecMontoReclamado: null,
        RecImporteAbonado: null,
      });
      this.form.controls['RecMontoReclamado'].disable();
      this.form.controls['RecMontoReclamado'].clearValidators();
      this.form.controls['RecImporteAbonado'].disable();
      this.form.controls['RecImporteAbonado'].clearValidators();
    }
    this.form.controls['RecMontoReclamado'].updateValueAndValidity();
    this.form.controls['RecImporteAbonado'].updateValueAndValidity();
  }
  recImporteAbonadoChange(): void {
    if (this.getRecImporteAbonado?.value > 0) {
      this.form.controls['RecFechaAbonoImporte'].enable();
      this.form.controls['RecFechaAbonoImporte'].setValidators(
        Validators.required
      );
    } else {
      this.form.patchValue({
        RecFechaAbonoImporte: null,
      });
      this.form.controls['RecFechaAbonoImporte'].disable();
      this.form.controls['RecFechaAbonoImporte'].clearValidators();
    }
    this.form.controls['RecFechaAbonoImporte'].updateValueAndValidity();
  }
  mapErrorsToFields(errors: string[]): ReclamacionREUNEError[] {
    return errors.map((error) => {
      const cleanedError = error
        .replace(' es obligatorio', '')
        .replace('.', '');
      for (const [key, field] of Object.entries(this.errorMapping)) {
        if (cleanedError.includes(key)) {
          return { field, message: error };
        }
      }
      return { field: null, message: error };
    });
  }
}
