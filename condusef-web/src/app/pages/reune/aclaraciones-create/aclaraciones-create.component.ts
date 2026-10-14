

import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription, firstValueFrom } from 'rxjs';
import { Causa, EntidadFed, MedioRecepcion, Municipio, NivelAtencion, Periodo, Producto, Colonia, CodigoPostal, AclaracionREUNE, AclaracionREUNEError } from '../../../core/models';
import { CatalogosService, AclaracionesService, FoliosService, PeriodosService } from '../../../core/services';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { NgbAlertModule, NgbTooltipModule, NgbDatepickerModule, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { ngbDateStructToDate, ngbDateToString_yyyy_mm_dd, String_dd_yy_yyyyToNgbDate } from '../../../app.helpers';

@Component({
  selector: 'app-aclaraciones-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NgbAlertModule, NgbTooltipModule, NgbDatepickerModule],
  templateUrl: './aclaraciones-create.component.html',
  styleUrl: './aclaraciones-create.component.scss'
})
export class AclaracionesReuneCreateComponent implements OnInit, OnDestroy {
  form: FormGroup;
  mediosRecepcion: MedioRecepcion[] = [];
  nivelesAtencion: NivelAtencion[] = [];
  productos: Producto[] = [];
  causas: Causa[] = [];
  entidadesFed: EntidadFed[] = [];
  codigosPostales: CodigoPostal[] = [];
  colonias: Colonia[] = [];
  localidades: Colonia[] = [];
  municipios: Municipio[] = [];
  origen: string = '';
  generarFolio: boolean = true;
  errores: string[] = [];
  erroresMapeados: AclaracionREUNEError[] = [];
  placement = 'bottom';
  inicio_periodo!: NgbDateStruct;
  fin_periodo!: NgbDateStruct;
  model1!: NgbDateStruct;
  model2!: NgbDateStruct;
  model3!: NgbDateStruct;
  model4!: NgbDateStruct;

  editar: boolean = false;
  periodo?: Periodo;
  registro?: AclaracionREUNE;
  errorMapping: { [key: string]: keyof AclaracionREUNE };

  // Controla la visibilidad de los errores de validación de CONDUSEF
  showCode: boolean = true; 

  private subscription?: Subscription;

  private readonly catalogosService = inject(CatalogosService);
  private readonly aclaracionesService = inject(AclaracionesService);
  private readonly folioService = inject(FoliosService);
  private readonly toastr = inject(ToastrService);
  private readonly periodoService = inject(PeriodosService);
  private ngxLoader = inject(NgxUiLoaderService);

  constructor() {
    this.errorMapping = {
      "Fecha de Aclaración": "AclaracionFechaAclaracion",
      "Fecha de Atención": "AclaracionFechaAtencion",
      "Medio de recepción o canal": "AclaracionMedioRecepcionCanal",
      "Producto y la Causa": "AclaracionProductoServicio",
      "Fecha de Recepción": "AclaracionFechaResolucion",
      "Fecha de Notificación": "AclaracionFechaNotifiUsuario",
      "Código Postal": "AclaracionCodigoPostal",
      "Municipio": "AclaracionMunicipioAlcaldia",
      "Localidad": "AclaracionLocalidad",
      "Colonia": "AclaracionColonia",
      "Monto reclamado": "AclaracionMontoReclamado",
      "AclaracionSexo": "AclaracionSexo",
      "AclaracionEdad": "AclaracionEdad",
      "Nivel de atención o contacto": "AclaracionNivelAtencion",
      "Folio asignado por condusef": "AclaracionFolioCondusef",
      "ReversaSIGE": "AclaracionReversa"
    };

    this.form = new FormGroup({
      Id: new FormControl<number | null>(null),
      AclaracionTrimestre: new FormControl<number | null>(null, [Validators.required]),
      AclaracionNumero: new FormControl<number | null>(null, [Validators.required]),
      AclaracionFolioAtencion: new FormControl<string | null>(null, [Validators.required]),
      AclaracionEstadoConPend: new FormControl<number | null>(null, [Validators.required]),
      AclaracionFechaAclaracion: new FormControl<string | null>(null, [Validators.required]),
      AclaracionFechaAtencion: new FormControl<string | null>(null),
      AclaracionMedioRecepcionCanal: new FormControl<number | null>(null, [Validators.required]),
      AclaracionProductoServicio: new FormControl<string | null>(null, [Validators.required]),
      AclaracionCausaMotivo: new FormControl<string | null>(null, [Validators.required]),
      AclaracionFechaResolucion: new FormControl<string | null>(null),
      AclaracionFechaNotifiUsuario: new FormControl<string | null>(null),
      AclaracionEntidadFederativa: new FormControl<number | null>(null, [Validators.required]),
      AclaracionCodigoPostal: new FormControl<number | null>(null),
      AclaracionMunicipioAlcaldia: new FormControl<number | null>(null, [Validators.required]),
      AclaracionLocalidad: new FormControl<number | null>(null),
      AclaracionColonia: new FormControl<number | null>(null),
      AclaracionMonetario: new FormControl<string | null>(null, [Validators.required]),
      AclaracionMontoReclamado: new FormControl<number | null>(null),
      AclaracionPori: new FormControl<string | null>(null, [Validators.required]),
      AclaracionTipoPersona: new FormControl<number | null>(null, [Validators.required]),
      AclaracionSexo: new FormControl<string | null>(null),
      AclaracionEdad: new FormControl<number | null>(null),
      AclaracionNivelAtencion: new FormControl<number | null>(null),
      AclaracionFolioCondusef: new FormControl<string | null>(null),
      AclaracionReversa: new FormControl<number | null>(null),
      AclaracionOperacionExtranjero: new FormControl<string | null>(null, [Validators.required]),
      Sucursal: new FormControl<number | null>(null),
      Estatus: new FormControl<number | null>(null)
    });
  }

  // Getters para acceso rápido a los controles
  get getAclaracionMedioRecepcionCanal() { return this.form.get('AclaracionMedioRecepcionCanal'); }
  get getAclaracionEntidadFederativa() { return this.form.get('AclaracionEntidadFederativa'); }
  get getAclaracionCodigoPostal() { return this.form.get('AclaracionCodigoPostal'); }
  get getAclaracionColonia() { return this.form.get('AclaracionColonia'); }
  get getAclaracionEstadoConPend() { return this.form.get('AclaracionEstadoConPend'); }
  get getAclaracionProductoServicio() { return this.form.get('AclaracionProductoServicio'); }
  get getAclaracionReversa() { return this.form.get('AclaracionReversa'); }
  get getAclaracionFechaAtencion() { return this.form.get('AclaracionFechaAtencion'); }
  get getAclaracionFechaResolucion() { return this.form.get('AclaracionFechaResolucion'); }
  get getAclaracionTipoPersona() { return this.form.get('AclaracionTipoPersona'); }
  get getAclaracionMonetario() { return this.form.get('AclaracionMonetario'); }

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
    this.obtenerAclaracionProductoServicios();
    this.obtenerPeriodoActivo();

    if (this.editar) {
      await this.inicializarEdicion();
      this.estadoChange();
      this.obtenerCausas();
    } else {
      await this.inicializarCaptura();
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  // --- LÓGICA DE INICIALIZACIÓN ---

  async inicializarCaptura(): Promise<void> {
    const nuevoFolio = await this.folioService.siguienteFolio('reune');
    this.form.patchValue({
      AclaracionTrimestre: this.periodo?.mes,
      AclaracionNumero: 1,
      AclaracionFolioAtencion: nuevoFolio,
      AclaracionFechaAclaracion: formatDate(new Date(), "yyyy-MM-dd", "en"),
    });

    // Iniciar el estado visual de los campos
    this.medioChange(); 

    ['AclaracionFechaAtencion', 'AclaracionFechaNotifiUsuario', 'AclaracionReversa', 'AclaracionFolioCondusef', 'AclaracionNivelAtencion']
      .forEach(c => this.form.controls[c].disable());
  }

  async inicializarEdicion(): Promise<void> {
    this.generarFolio = !this.registro?.Reportado;

    // Sincronización de fechas
    if (this.registro!.AclaracionFechaAclaracion) {
      this.model1 = String_dd_yy_yyyyToNgbDate(this.registro!.AclaracionFechaAclaracion);
      this.form.patchValue({ AclaracionFechaAclaracion: formatDate(new Date(this.reorderDateString(this.registro!.AclaracionFechaAclaracion)), "yyyy-MM-dd", "en") });
    }
    if (this.registro!.AclaracionFechaAtencion) {
      this.model2 = String_dd_yy_yyyyToNgbDate(this.registro!.AclaracionFechaAtencion);
      this.form.patchValue({ AclaracionFechaAtencion: formatDate(new Date(this.reorderDateString(this.registro!.AclaracionFechaAtencion)), "yyyy-MM-dd", "en") });
    }
    if (this.registro!.AclaracionFechaResolucion) {
      this.model3 = String_dd_yy_yyyyToNgbDate(this.registro!.AclaracionFechaResolucion);
      this.form.patchValue({ AclaracionFechaResolucion: formatDate(new Date(this.reorderDateString(this.registro!.AclaracionFechaResolucion)), "yyyy-MM-dd", "en") });
    }
    if (this.registro!.AclaracionFechaNotifiUsuario) {
      this.model4 = String_dd_yy_yyyyToNgbDate(this.registro!.AclaracionFechaNotifiUsuario);
      this.form.patchValue({ AclaracionFechaNotifiUsuario: formatDate(new Date(this.reorderDateString(this.registro!.AclaracionFechaNotifiUsuario)), "yyyy-MM-dd", "en") });
    }

    // Cargamos geografía guardada
    this.form.patchValue({
      AclaracionEntidadFederativa: this.registro?.AclaracionEntidadFederativa,
      AclaracionCodigoPostal: this.registro?.AclaracionCodigoPostal,
    });

    // Forzamos carga de catálogos geográficos para que el Municipio aparezca
    await this.obtenerColonias();
    this.obtenerMunicipios(); 

    this.form.patchValue({
      Id: this.registro?.Id,
      AclaracionTrimestre: this.registro?.AclaracionTrimestre,
      AclaracionNumero: this.registro?.AclaracionNumero,
      AclaracionFolioAtencion: this.registro?.AclaracionFolioAtencion,
      AclaracionEstadoConPend: this.registro?.AclaracionEstadoConPend,
      AclaracionMedioRecepcionCanal: this.registro?.AclaracionMedioRecepcionCanal,
      AclaracionProductoServicio: this.registro?.AclaracionProductoServicio,
      AclaracionCausaMotivo: this.registro?.AclaracionCausaMotivo,
      AclaracionMunicipioAlcaldia: this.registro?.AclaracionMunicipioAlcaldia,
      AclaracionLocalidad: this.registro?.AclaracionLocalidad,
      AclaracionColonia: this.registro?.AclaracionColonia,
      AclaracionMonetario: this.registro?.AclaracionMonetario,
      AclaracionMontoReclamado: this.registro?.AclaracionMontoReclamado,
      AclaracionPori: this.registro?.AclaracionPori,
      AclaracionTipoPersona: this.registro?.AclaracionTipoPersona,
      AclaracionSexo: this.registro?.AclaracionSexo,
      AclaracionEdad: this.registro?.AclaracionEdad,
      AclaracionNivelAtencion: this.registro?.AclaracionNivelAtencion,
      AclaracionFolioCondusef: this.registro?.AclaracionFolioCondusef,
      AclaracionReversa: this.registro?.AclaracionReversa,
      AclaracionOperacionExtranjero: this.registro?.AclaracionOperacionExtranjero,
      Sucursal: this.registro?.Sucursal,
      Estatus: this.registro?.Estatus
    });

    this.tipoPersonaChange();
    this.medioChange(); 
    this.coloniaChange();
  }

  // --- MÉTODOS GEOGRÁFICOS ---

  async obtenerColonias(): Promise<void> {
    const cp = this.getAclaracionCodigoPostal?.value;
    if (!cp || cp === 0) return;

    try {
      const response = await firstValueFrom(this.catalogosService.obtenerColonias(cp));
      this.colonias = response;
      if (this.colonias.length > 0) {
        // Corrección Bug 3: Sincronizar Estado con el CP
        this.form.get('AclaracionEntidadFederativa')?.setValue(this.colonias[0].estadoId, { emitEvent: false });
        this.obtenerMunicipios();
      }
    } catch (error) { this.toastr.error('Error al cargar colonias'); }
  }

  obtenerMunicipios(): void {
    const estado = this.getAclaracionEntidadFederativa?.value;
    let cp = this.getAclaracionCodigoPostal?.value;
    
    // Si el CP es 0 (como en SIGE), enviamos nulo al servicio para que traiga todos los municipios del estado
    if (cp === 0) cp = null;

    if (estado) {
      this.subscription = this.catalogosService.obtenerMunicipios(estado, cp).subscribe({
        next: (response) => {
          this.municipios = response; // Corregido: Cargamos siempre la lista completa
        },
        error: (error) => this.toastr.error('Error al cargar municipios')
      });
    }
  }

  obtenerCodigosPostales(): void {
    const estadoId = this.getAclaracionEntidadFederativa?.value;
    if (estadoId) {
      this.subscription = this.catalogosService.obtenerCodigosPostales(estadoId).subscribe(res => this.codigosPostales = res);
    }
  }

  // --- EVENTOS DEL TEMPLATE ---

  codigoPostalChange(): void { this.obtenerColonias(); }
  entidadFedChange(): void { this.obtenerCodigosPostales(); this.obtenerMunicipios(); }

  coloniaChange(): void {
    const colSeleccionada = this.colonias.find(col => col.coloniaId == this.getAclaracionColonia?.value);
    if (colSeleccionada) {
      // Corrección Bugs 1 y 2: Relación estricta de jerarquía geográfica
      this.localidades = [colSeleccionada];
      this.municipios = [{ 
        "estadoId": colSeleccionada.estadoId, 
        "municipioId": colSeleccionada.municipioId, 
        "municipio": colSeleccionada.municipio 
      }];
      
      this.form.patchValue({
        AclaracionEntidadFederativa: colSeleccionada.estadoId,
        AclaracionMunicipioAlcaldia: colSeleccionada.municipioId,
        AclaracionLocalidad: colSeleccionada.tipoLocalidadId
      });
    }
  }

  medioChange(): void {
    const medio = this.getAclaracionMedioRecepcionCanal?.value;
    const controls = this.form.controls;

    // Geografía (UNE=5, Sucursal=3, Oficina=17)
    const esPresencial = [3, 5, 17].includes(medio);
    if (esPresencial) {
      controls['AclaracionCodigoPostal'].enable();
      controls['AclaracionCodigoPostal'].setValidators(Validators.required);
      controls['AclaracionLocalidad'].enable();
      controls['AclaracionColonia'].enable();
    } else {
      // Bloqueamos pero no borramos valores para evitar CP 0 en el envío de SIGE
      controls['AclaracionCodigoPostal'].disable();
      controls['AclaracionLocalidad'].disable();
      controls['AclaracionColonia'].disable();
      controls['AclaracionCodigoPostal'].clearValidators();
    }

    // CONDUSEF / SIGE
    if ([6, 7].includes(medio)) {
      controls['AclaracionFolioCondusef'].enable();
      controls['AclaracionFolioCondusef'].setValidators(Validators.required);
      if (medio === 6) {
        controls['AclaracionReversa'].enable();
        // Corrección Bug 4: Evitar nulo en Reversa para medio 6 (CONDUSEF)
        if (this.getAclaracionReversa?.value === null) controls['AclaracionReversa'].setValue(0);
      } else {
        controls['AclaracionReversa'].disable();
        this.form.get('AclaracionReversa')?.setValue(null);
      }
    } else {
      this.form.patchValue({ AclaracionReversa: null, AclaracionFolioCondusef: null });
      controls['AclaracionReversa'].disable();
      controls['AclaracionFolioCondusef'].disable();
      controls['AclaracionFolioCondusef'].clearValidators();
    }

    ['AclaracionCodigoPostal', 'AclaracionLocalidad', 'AclaracionColonia', 'AclaracionReversa', 'AclaracionFolioCondusef']
      .forEach(c => controls[c].updateValueAndValidity());
  }

  estadoChange(): void {
    this.showCode = !this.showCode;
    const controls = this.form.controls;
    if (this.getAclaracionEstadoConPend?.value == 2) {
      ['AclaracionFechaAtencion', 'AclaracionFechaNotifiUsuario', 'AclaracionNivelAtencion', 'AclaracionFechaResolucion'].forEach(c => {
        controls[c].enable();
        controls[c].setValidators(Validators.required);
      });
    } else {
      ['AclaracionFechaAtencion', 'AclaracionFechaNotifiUsuario', 'AclaracionNivelAtencion', 'AclaracionFechaResolucion'].forEach(c => {
        this.form.get(c)?.setValue(null);
        controls[c].disable();
        controls[c].clearValidators();
      });
    }
  }

  // --- ACCIONES PRINCIPALES ---

  async nuevoAclaracionFolioAtencion(): Promise<void> {
    Swal.fire({ title: 'Generar Folio', text: '¿Deseas generar un nuevo folio de atención?', showCancelButton: true }).then(async (res) => {
      if (res.value) {
        this.folioService.desecharFolio('reune');
        this.form.patchValue({ AclaracionFolioAtencion: await this.folioService.siguienteFolio('reune') });
      }
    });
  }

  guardarRegistro(): void {
    if (this.validarFechas()) {
      this.ngxLoader.start();
      //getRawValue() asegura que se envíen CP y Colonia aunque estén bloqueados (grises)
      const rawData = this.form.getRawValue();
      this.aclaracionesService.crearAclaracion(JSON.stringify(rawData, this.replacerFn)).subscribe({
        next: () => { this.limpiarCaptura(); this.ngxLoader.stop(); this.toastr.success('Registro guardado'); },
        error: (err) => { this.ngxLoader.stop(); this.toastr.error(err); }
      });
    }
  }

  editarRegistro(): void {
    if (this.validarFechas()) {
      this.ngxLoader.start();
      const rawData = this.form.getRawValue();
      this.aclaracionesService.modificarAclaracion(JSON.stringify(rawData, this.replacerFn)).subscribe({
        next: () => { this.ngxLoader.stop(); this.toastr.success('Registro editado'); },
        error: (err) => { this.ngxLoader.stop(); this.toastr.error(err); }
      });
    }
  }

  // --- AYUDANTES Y FORMATOS ---

  private replacerFn(key: string, value: any) {
    if (key === "AclaracionCausaMotivo") return value?.toString();
    if (key === "AclaracionReversa") return value ? 1 : 0;
    if (["AclaracionFechaAclaracion", "AclaracionFechaAtencion", "AclaracionFechaResolucion", "AclaracionFechaNotifiUsuario"].includes(key)) {
      return ngbDateToString_yyyy_mm_dd(value);
    }
    return value;
  }

  private validarFechas(): boolean {
    const fAtencion = this.getAclaracionFechaAtencion?.value;
    const fResolucion = this.getAclaracionFechaResolucion?.value;
    if (fAtencion && fResolucion) {
      if (ngbDateStructToDate(fAtencion).getTime() > ngbDateStructToDate(fResolucion).getTime()) {
        this.toastr.error('La fecha de resolución no puede ser anterior a la de atención');
        return false;
      }
    }
    return true;
  }

  tipoPersonaChange(): void {
    const controls = this.form.controls;
    if (this.getAclaracionTipoPersona?.value == 1) {
      controls['AclaracionSexo'].enable(); controls['AclaracionEdad'].enable();
    } else {
      this.form.patchValue({ AclaracionSexo: null, AclaracionEdad: null });
      controls['AclaracionSexo'].disable(); controls['AclaracionEdad'].disable();
    }
  }

  aclaracionMonetarioChange(): void {
    if (this.getAclaracionMonetario?.value == "SI") this.form.controls['AclaracionMontoReclamado'].enable();
    else this.form.controls['AclaracionMontoReclamado'].disable();
  }

  obtenerMediosRecepcion(): void { this.catalogosService.obtenerMediosRecepcion('reune').subscribe(res => this.mediosRecepcion = res); }
  obtenerNivelesAtencion(): void { this.catalogosService.obtenerNivelesAtencion('reune').subscribe(res => this.nivelesAtencion = res); }
  obtenerAclaracionProductoServicios(): void { this.catalogosService.obtenerProductos('reune').subscribe(res => this.productos = res); }
  obtenerEntidadesFed(): void { this.catalogosService.obtenerEntidadesFed().subscribe(res => this.entidadesFed = res); }
  obtenerCausas(): void { this.catalogosService.obtenerCausas(this.getAclaracionProductoServicio?.value, 'reune', 3).subscribe(res => this.causas = res); }
  
  obtenerPeriodoActivo(): void {
    this.periodoService.obtenerPeriodoActivo('reune').then((p) => {
      this.periodo = p;
      this.form.patchValue({ AclaracionTrimestre: p.mes });
      this.inicio_periodo = String_dd_yy_yyyyToNgbDate(p.fecha_inicial);
      this.fin_periodo = String_dd_yy_yyyyToNgbDate(p.fecha_final);
    });
  }

  reorderDateString(date: string): string {
    if (!date) return "";
    let d = date.split("/"); return `${d[2]}/${d[1]}/${d[0]}`;
  }

  mapErrorsToFields(errors: string[]): AclaracionREUNEError[] {
    return errors.map(error => {
      const clean = error.replace(" es obligatorio", "").replace(".", "");
      for (const [k, v] of Object.entries(this.errorMapping)) { if (clean.includes(k)) return { field: v, message: error }; }
      return { field: null, message: error };
    });
  }

  async limpiarCaptura(): Promise<void> {
    this.folioService.desecharFolio('reune');
    this.form.reset({ 
      AclaracionTrimestre: this.periodo?.mes, 
      AclaracionNumero: 1, 
      AclaracionFolioAtencion: await this.folioService.siguienteFolio('reune'), 
      AclaracionFechaAclaracion: formatDate(new Date(), "yyyy-MM-dd", "en") 
    });
    this.medioChange();
  }
  
}