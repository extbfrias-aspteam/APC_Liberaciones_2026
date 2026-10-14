import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { firstValueFrom, Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { Causa, EntidadFed, MedioRecepcion, Municipio, NivelAtencion, Periodo, Producto, Colonia, CodigoPostal, Queja } from '../../../../core/models';
import { CatalogosService, QuejasService, FoliosService } from '../../../../core/services';
import { ngbDateToString_yyyy_mm_dd, dateStringToString_yyyy_mm_dd } from '../../../../app.helpers';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-quejas-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NgbModule],
  templateUrl: './quejas-create.component.html',
  styleUrl: './quejas-create.component.scss'
})
export class QuejasCreateComponent implements OnInit {
  form: FormGroup;
  mediosRecepcion: MedioRecepcion[];
  nivelesAtencion: NivelAtencion[];
  productos: Producto[];
  causas: Causa[];
  entidadesFed: EntidadFed[];
  codigosPostales: CodigoPostal[];
  colonias: Colonia[];
  localidades: Colonia[]; // la localidad se extrae de la colonia
  municipios: Municipio[];
  quejas: Queja[];
  generarFolio: boolean;
  
  editar: boolean;
  periodo?: Periodo;
  queja?: Queja;

  private subscription?: Subscription;

  private readonly catalogosService = inject(CatalogosService);
  private readonly quejasService    = inject(QuejasService);
  private readonly folioService     = inject(FoliosService);
  private readonly toastr           = inject(ToastrService);
  private readonly ngxLoader        = inject(NgxUiLoaderService);
  
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
    this.quejas          = [];
    this.editar          = false;
    this.generarFolio    = true;

    this.form = new FormGroup({
      nombreCliente:     new FormControl<string | null>(null, [Validators.required]),
      correoCliente:     new FormControl<string | null>(null, [Validators.required, Validators.email]),
      telefonoCliente:   new FormControl<number | null>(null, [Validators.required, Validators.minLength(10), Validators.maxLength(10)]),
      sucursal:          new FormControl<string | null>(null, [Validators.required]),
      numCredito:        new FormControl<string | null>(null, [Validators.required]),
      despachoCobranza:  new FormControl<string | null>(null, [Validators.required]),
      numMes:            new FormControl<number | null>(null, [Validators.required]),
      numQuejas:         new FormControl<number | null>(null, [Validators.required, Validators.min(1)]),
      numFolio:          new FormControl<string | null>(null, [Validators.required]),
      fechaRecepcion:    new FormControl<string | null>(null, [Validators.required]),
      idMedioRecepcion:  new FormControl<number | null>(null, [Validators.required]),
      idNivelAtencion:   new FormControl<number | null>(null, [Validators.required]),
      idProducto:        new FormControl<string | null>(null, [Validators.required]),
      idCausa:           new FormControl<string | null>(null, [Validators.required]),
      pori:              new FormControl<string | null>(null, [Validators.required]),
      estatus:           new FormControl<number | null>(null, [Validators.required]),
      idEntidadFed:      new FormControl<number | null>(null, [Validators.required]),
      idMunicipio:       new FormControl<number | null>(null, [Validators.required]),
      idLocalidad:       new FormControl<number | null>(null),
      idColonia:         new FormControl<number | null>(null, [Validators.required]),
      codPostal:         new FormControl<number | null>(null, [Validators.required, Validators.minLength(5), Validators.maxLength(5)]),
      idTipoPersona:     new FormControl<number | null>(null, [Validators.required]),
      personaSexo:       new FormControl<string | null>(null),
      personaEdad:       new FormControl<number | null>(null),
      fechaResolucion:   new FormControl<string | null>(null),
      fechaNotificacion: new FormControl<string | null>(null),
      idRespuestaRes:    new FormControl<number | null>(null),
      numPenalizacion:   new FormControl<number | null>(null),
      idPenalizacion:    new FormControl<number | null>(null),
      idEmpresa:         new FormControl<number | null>(null),
    });
  }

  /*
    Propiedades
  */

  get getNumFolio() {
    return this.form.get('numFolio');
  }

  get getProducto() {
    return this.form.get('idProducto');
  }

  get getTipoPersona() {
    return this.form.get('idTipoPersona');
  }

  get getEntidadFed() {
    return this.form.get('idEntidadFed');
  }

  get getMunicipio() {
    return this.form.get('idMunicipio');
  }

  get getCodigoPostal() {
    return this.form.get('codPostal');
  }

  get getColonia() {
    return this.form.get('idColonia');
  }

  get getEstado() {
    return this.form.get('estatus');
  }

  get getFechaResolucion() {
    return this.form.get('fechaResolucion');
  }

  get getFechaRecepcion() {
    return this.form.get('fechaRecepcion');
  }

  get getFechaNotificacion() {
    return this.form.get('fechaNotificacion');
  }

  get getNumPenalizacion() {
    return this.form.get('numPenalizacion');
  }

  async ngOnInit(): Promise<void> {
    this.periodo = history.state.periodo;
    this.editar  = history.state.editar;
    this.queja   = history.state.queja;

    this.obtenerMediosRecepcion();
    this.obtenerNivelesAtencion();
    this.obtenerEntidadesFed();
    this.obtenerProductos();

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
    const nuevoFolio = await this.siguienteFolioQueja();

    this.form.patchValue({
      numMes: this.periodo?.mes,
      numQuejas: 1,
      numFolio: nuevoFolio,
      fechaRecepcion: {
        year:  this.periodo!.anio,
        month: this.periodo!.mes,
        day:   1,
      },
      numPenalizacion: null,
    });

    this.form.controls['personaSexo'].disable();
    this.form.controls['personaEdad'].disable();
    this.form.controls['idRespuestaRes'].disable();
    this.form.controls['fechaResolucion'].disable();
    this.form.controls['fechaNotificacion'].disable();
    this.form.controls['numPenalizacion'].disable();
    this.form.controls['idPenalizacion'].disable();
    this.form.controls['numMes'].disable();
  }

  async inicializarEdicion(): Promise<void> {
    /*
      Asignando fechas al formulario de edición

      Las fechas vienen en formato dd/MM/yyyy, se separan en un array de numeros para poder 
      asignarse a un NgbDateStruct
    */
    if (this.queja!.fechaRecepcion) {
      const feRecepçionArray = this.queja!.fechaRecepcion.split('/').map(Number);
      this.form.patchValue({
        fechaRecepcion: {
          year:  feRecepçionArray[2],
          month: feRecepçionArray[1],
          day:   feRecepçionArray[0],
        },
      });
    }

    if (this.queja!.fechaResolucion) {
      let feResolucionArray = this.queja!.fechaResolucion.split('/').map(Number);
      this.form.patchValue({
        fechaResolucion: {
          year:  feResolucionArray[2],
          month: feResolucionArray[1],
          day:   feResolucionArray[0],
        },
      });
    }

    if (this.queja!.fechaNotificacion) {
      let feNotificacionArray = this.queja!.fechaNotificacion.split('/').map(Number);
      this.form.patchValue({
        fechaNotificacion: {
          year:  feNotificacionArray[2],
          month: feNotificacionArray[1],
          day:   feNotificacionArray[0],
        },
      });
    }

    /*
      Deshabilitando la generacion de un nuevo folio
    */
    this.generarFolio = false;
    
    /*
      Asignando datos con llenado en cascada
    */
    this.form.patchValue({
      idEntidadFed: this.queja?.idEntidadFed,
      codPostal:    this.queja?.codPostal,
    });

    this.form.patchValue({
      idMunicipio:  this.queja?.idMunicipio,
      idColonia:    this.queja?.idColonia,
      idLocalidad:  this.queja?.idLocalidad,
      idProducto:   this.queja?.idProducto,
    });

    setTimeout(() => {
      this.coloniaChange();   
    }, 1500);

    await this.obtenerColonias();

    this.obtenerCausas();

    this.form.patchValue({
      idCausa:      this.queja?.idCausa,
    });

    /*
      Asignando los datos restantes
    */
    this.form.patchValue({
      nombreCliente:     this.queja?.nombreCliente,
      correoCliente:     this.queja?.correoCliente,
      telefonoCliente:   this.queja?.telefonoCliente,
      sucursal:          this.queja?.sucursal,
      numCredito:        this.queja?.numCredito,
      despachoCobranza:  this.queja?.despachoCobranza,
      numMes:           this.queja?.numMes,
      numQuejas:         this.queja?.numQuejas,
      numFolio:          this.queja?.numFolio,
      idMedioRecepcion:  this.queja?.idMedioRecepcion,
      idNivelAtencion:   this.queja?.idNivelAtencion,
      pori:              this.queja?.pori,
      estatus:           this.queja?.estatus,
      idTipoPersona:     this.queja?.idTipoPersona,
      personaSexo:       this.queja?.personaSexo,
      personaEdad:       this.queja?.personaEdad,
      idRespuestaRes:    this.queja?.idRespuestaRes,
      numPenalizacion:   this.queja?.numPenalizacion,
      idPenalizacion:    this.queja?.idPenalizacion,
      idEmpresa:         this.queja?.idEmpresa,
    });

    this.tipoPersonaChange();
    this.estadoChange();

    this.form.controls['numMes'].disable();
  }

  obtenerMediosRecepcion(): void {
      this.subscription = this.catalogosService.obtenerMediosRecepcion('redeco').subscribe({
        next: (response: MedioRecepcion[]) => {
          this.mediosRecepcion = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  obtenerNivelesAtencion(): void {
      this.subscription = this.catalogosService.obtenerNivelesAtencion('redeco').subscribe({
        next: (response: NivelAtencion[]) => {
          this.nivelesAtencion = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  obtenerProductos(): void {
      this.subscription = this.catalogosService.obtenerProductos('redeco').subscribe({
        next: (response: Producto[]) => {
          this.productos = response;
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Productos, ha ocurrido un error...');
        }
      });
  }

  obtenerCausas(): void {
      this.subscription = this.catalogosService.obtenerCausas(this.getProducto?.value, 'redeco').subscribe({
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
      this.subscription = this.catalogosService.obtenerMunicipios(this.getEntidadFed?.value, this.getCodigoPostal?.value).subscribe({
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
      await firstValueFrom(this.catalogosService.obtenerColonias(this.getCodigoPostal?.value))
      .then((response: Colonia[]) => {
        this.colonias = response;
        if (this.colonias.length > 0) {
          this.form.controls['idEntidadFed'].setValue(this.colonias[0].estadoId);
        }
        this.obtenerMunicipios();
      })
      if (this.colonias.length > 0) {
        this.form.controls['idEntidadFed'].setValue(this.colonias[0].estadoId);
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

  async siguienteFolioQueja() {
    const siguienteFolio = await this.folioService.siguienteFolio('redeco').catch((error)=>{
      this.toastr.error(`${error.error}`, 'Ha ocurrido un error...');
    });

    return siguienteFolio;
  }

  desecharFolioQueja() {
    this.folioService.desecharFolio('redeco');
  }

  async nuevoNumFolio(): Promise<void> {
      Swal.fire({
        title: 'Generar Folio',
        text: '¿Seguro que deseas generar un nuevo número de folio?',
        showCancelButton: true,
        confirmButtonText: 'Si, Deseo Generarlo',
        cancelButtonText: 'No'
      }).then(async (result) => {
        if (result.value) {
          // desechamos el folio actual y se genera un nuevo folio
          this.desecharFolioQueja();
          //this.folioService.desecharFolio('redeco');
          //const nuevoFolio = await this.folioService.siguienteFolio('redeco');
          const nuevoFolio = await this.siguienteFolioQueja();
          // se asigna el nuevo folio al control
          this.form.patchValue({
            numFolio: nuevoFolio,
          });
        };
      });
  }

  async limpiarCaptura(): Promise<void> {
    // desechamos el folio actual y se genera un nuevo folio
    this.desecharFolioQueja();
    const nuevoFolio = await this.siguienteFolioQueja();
    // se asignan los valores en los controles
    this.form.patchValue({
      nombreCliente:     null,
      correoCliente:     null,
      telefonoCliente:   null,
      sucursal:          null,
      numCredito:        null,
      despachoCobranza:  null,
      numMes:            this.periodo?.mes,
      numQuejas:         1,
      numFolio:          nuevoFolio,
      fechaRecepcion: {
        year:  this.periodo!.anio,
        month: this.periodo!.mes,
        day:   1
      },
      idMedioRecepcion:  null,
      idNivelAtencion:   null,
      idProducto:        null,
      idCausa:           null,
      pori:              null,
      estatus:           null,
      idEntidadFed:      null,
      idMunicipio:       null,
      idLocalidad:       null,
      idColonia:         null,
      codPostal:         null,
      idTipoPersona:     null,
      personaSexo:       null,
      personaEdad:       null,
      fechaResolucion:   null,
      fechaNotificacion: null,
      idRespuestaRes:    null,
      numPenalizacion:   null,
      idPenalizacion:    null,
    });

    this.form.markAsUntouched();
    this.form.markAsPristine();
  }

  guardarQueja(): void {

    // Validación de fechas
    const feInicial   = new Date(dateStringToString_yyyy_mm_dd(this.periodo!.fecha_inicial));
    const feFinal     = new Date(dateStringToString_yyyy_mm_dd(this.periodo!.fecha_final));
    const feRecepcion = new Date(this.getFechaRecepcion?.value.year, this.getFechaRecepcion?.value.month - 1, this.getFechaRecepcion?.value.day);

    if (feRecepcion < feInicial || feRecepcion > feFinal) {
      this.toastr.error(`La fecha de recepción debe estar en el rango inicial y final del periodo abierto`, 'Fecha de recepción');
      return;
    }

    if (this.getFechaResolucion?.value) {
      const feResolucion = new Date(this.getFechaResolucion?.value.year, this.getFechaResolucion?.value.month - 1, this.getFechaResolucion?.value.day);

      if (feResolucion <= feRecepcion) {
        this.toastr.error(`La fecha de resolución no puede ser menor o igual a la fecha de recepción`, 'Fecha de resolución');
        return;
      }
    }

    const queja = this.createQuejaFromForm(this.form);

    this.ngxLoader.start();
    this.quejasService.crearQueja(JSON.stringify(queja)).subscribe({
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

  editarQueja(): void {

    // Validación de fechas
    const feInicial    = new Date(dateStringToString_yyyy_mm_dd(this.periodo!.fecha_inicial));
    const feFinal      = new Date(dateStringToString_yyyy_mm_dd(this.periodo!.fecha_final));
    const feRecepcion  = new Date(this.getFechaRecepcion?.value.year, this.getFechaRecepcion?.value.month - 1, this.getFechaRecepcion?.value.day);

    if (feRecepcion < feInicial || feRecepcion > feFinal) {
      this.toastr.error(`La fecha de recepción debe estar en el rango inicial y final del periodo abierto`, 'Fecha de recepción');
      return;
    }

    if (this.getFechaResolucion?.value) {
      const feResolucion = new Date(this.getFechaResolucion?.value.year, this.getFechaResolucion?.value.month - 1, this.getFechaResolucion?.value.day);

      if (feResolucion <= feRecepcion) {
        this.toastr.error(`La fecha de resolución no puede ser menor o igual a la fecha de recepción`, 'Fecha de resolución');
        return;
      }
    }

    const queja = this.createQuejaFromForm(this.form);

    this.ngxLoader.start();
    this.quejasService.modificarQueja(JSON.stringify(queja)).subscribe({
      next: () => {
        this.ngxLoader.stop();
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
  
  tipoPersonaChange(): void {
    // si tipo de persona es fisica
    if (this.getTipoPersona?.value == 1) {
      this.form.controls['personaSexo'].enable();
      this.form.controls['personaSexo'].setValidators(Validators.required);
      this.form.controls['personaEdad'].enable();
      this.form.controls['personaEdad'].setValidators([Validators.required, Validators.max(999), Validators.pattern("[0-9]+")]);
    } else {
      this.form.patchValue({
        personaSexo: null,
        personaEdad: null
      });
      this.form.controls['personaSexo'].disable();
      this.form.controls['personaSexo'].clearValidators();
      this.form.controls['personaEdad'].disable();
      this.form.controls['personaEdad'].clearValidators();
    }
    this.form.controls['personaSexo'].updateValueAndValidity();
    this.form.controls['personaEdad'].updateValueAndValidity();
  }

  estadoChange(): void {
    // si estado es concluido
    if (this.getEstado?.value == 2) {
      this.form.controls['idRespuestaRes'].enable();
      this.form.controls['idRespuestaRes'].setValidators(Validators.required);
      this.form.controls['fechaResolucion'].enable();
      this.form.controls['fechaResolucion'].setValidators(Validators.required);
      this.form.controls['fechaNotificacion'].enable();
      this.form.controls['fechaNotificacion'].setValidators(Validators.required);
      this.form.controls['numPenalizacion'].enable();
      this.form.controls['numPenalizacion'].setValidators([Validators.max(9999)]);
      this.form.controls['idPenalizacion'].enable();
    } else {
      this.form.patchValue({
        idRespuestaRes: null,
        fechaResolucion: null,
        fechaNotificacion: null,
        numPenalizacion: null,
        idPenalizacion: null,
      });
      this.form.controls['idRespuestaRes'].disable();
      this.form.controls['idRespuestaRes'].clearValidators();
      this.form.controls['fechaResolucion'].disable();
      this.form.controls['fechaResolucion'].clearValidators();
      this.form.controls['fechaNotificacion'].disable();
      this.form.controls['fechaNotificacion'].clearValidators();
      this.form.controls['numPenalizacion'].disable();
      this.form.controls['numPenalizacion'].clearValidators();
      this.form.controls['idPenalizacion'].disable();
      this.form.controls['idPenalizacion'].clearValidators();
    }
    this.form.controls['idRespuestaRes'].updateValueAndValidity();
    this.form.controls['fechaResolucion'].updateValueAndValidity();
    this.form.controls['fechaNotificacion'].updateValueAndValidity();
    this.form.controls['numPenalizacion'].updateValueAndValidity();
    this.form.controls['idPenalizacion'].updateValueAndValidity();
  }

  numPenalizacionChange(): void {
    if (this.getNumPenalizacion?.value !== null) {
      this.form.controls['idPenalizacion'].setValidators(Validators.required);
      this.form.controls['idPenalizacion'].updateValueAndValidity();
    } else {
      this.form.controls['idPenalizacion'].clearValidators();
      this.form.controls['idPenalizacion'].updateValueAndValidity();
    }
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
        idLocalidad: colSeleccionada?.tipoLocalidadId,
        idMunicipio: colSeleccionada?.municipioId
      });
    }
  }

  createQuejaFromForm(form: FormGroup): Queja {
    return <Queja>{
      nombreCliente:    form.get('nombreCliente')?.value,
      correoCliente:    form.get('correoCliente')?.value,
      telefonoCliente:  form.get('telefonoCliente')?.value,
      sucursal:         form.get('sucursal')?.value,
      numCredito:       form.get('numCredito')?.value,
      despachoCobranza: form.get('despachoCobranza')?.value,
      numMes:           form.get('numMes')?.value, 
      numQuejas:        form.get('numQuejas')?.value, 
      numFolio:         form.get('numFolio')?.value,
      fechaRecepcion:   ngbDateToString_yyyy_mm_dd(this.getFechaRecepcion?.value),
      idMedioRecepcion: form.get('idMedioRecepcion')?.value,
      idNivelAtencion:  form.get('idNivelAtencion')?.value,
      idProducto:       form.get('idProducto')?.value,
      producto:         form.get('producto')?.value,
      idCausa:          form.get('idCausa')?.value,
      causa:            form.get('causa')?.value,
      pori:             form.get('pori')?.value,
      estatus:          form.get('estatus')?.value,
      idEntidadFed:     form.get('idEntidadFed')?.value,
      idMunicipio:      form.get('idMunicipio')?.value,
      idLocalidad:      form.get('idLocalidad')?.value,
      idColonia:        form.get('idColonia')?.value,
      codPostal:        form.get('codPostal')?.value,
      idTipoPersona:    form.get('idTipoPersona')?.value,
      personaSexo:      form.get('personaSexo')?.value,
      personaEdad:      form.get('personaEdad')?.value,
      fechaResolucion:  this.getFechaResolucion?.value 
                          ? ngbDateToString_yyyy_mm_dd(this.getFechaResolucion?.value) 
                          : null,
      fechaNotificacion:this.getFechaNotificacion?.value 
                          ? ngbDateToString_yyyy_mm_dd(this.getFechaNotificacion?.value) 
                          : null,
      idRespuestaRes:   form.get('idRespuestaRes')?.value,
      numPenalizacion:  form.get('numPenalizacion')?.value,
      idPenalizacion:   form.get('idPenalizacion')?.value,
      reportado:        form.get('reportado')?.value,
      idEmpresa:        form.get('idEmpresa')?.value,
    }
  }
}