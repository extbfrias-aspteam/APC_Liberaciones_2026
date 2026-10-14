import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DragDropDirective } from '../../../../core/directives/drag-drop.directive';
import { EvidenciasService } from '../../../../core/services/evidencias.service';
import { ToastrService } from 'ngx-toastr';
import { Evidencia, Queja } from '../../../../core/models';
import { Subscription } from 'rxjs';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { FileSaverModule, FileSaverService } from 'ngx-filesaver';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-evidencias-upload-modal',
  standalone: true,
  imports: [CommonModule, DragDropDirective, NgxUiLoaderModule, FileSaverModule],
  templateUrl: './evidencias-upload-modal.component.html',
  styleUrl: './evidencias-upload-modal.component.scss'
})
export class EvidenciasUploadModalComponent implements OnInit, OnDestroy{
  @ViewChild('fileInput', {static: false}) fileInput: any;
  
  @Input() paramQueja: Queja | undefined;
  
  selected?: File;
  evidencias: Evidencia[] = [];
  extensionesValidas: string[] = environment.extensionesValidas;

  public  readonly activeModal       = inject(NgbActiveModal);
  private readonly evidenciasService = inject(EvidenciasService);
  private readonly toastr            = inject(ToastrService);
  private readonly ngxLoader         = inject(NgxUiLoaderService);
  private readonly fileSaverService  = inject(FileSaverService);

  private subscription?: Subscription;
  
  constructor() {  }

  ngOnInit(): void {
    if (!this.paramQueja) {
      return;
    }

    this.obtenerEvidencias();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  /*
    Archivo
  */

  cargarArchivo(): void {
    if (this.selected) {
      // se valida que la extensión del archivo este permitida
      if (!this.validarExtension(this.selected!.name)) {
        this.toastr.error(`las extensiones permitidas son: ${this.extensionesValidas.toString()}`, 'No es posible cargar este tipo de archivos');
        return;
      }

      // se inicia la carga del archivo
      this.ngxLoader.startLoader('loader-01');
      this.evidenciasService.upload(this.selected, this.paramQueja!.numFolio).subscribe({
        next: (response: any) => {
          this.ngxLoader.stopLoader('loader-01');
          this.toastr.success('success');

          // se cargan nuevamente las evidencias
          this.obtenerEvidencias();
        },
        error: (error: any) => {
          this.ngxLoader.stopLoader('loader-01');
          this.toastr.error(error);
        }
      });
    }
  }

  descargarArchivo(doc: Evidencia): void {
    this.ngxLoader.startLoader('loader-01');
    this.evidenciasService.download(this.paramQueja!.numFolio, doc.id).subscribe({
      next: (response: any) => {
        this.fileSaverService.save(response, doc.nombreDoc);
        this.ngxLoader.stopLoader('loader-01');
        this.toastr.success('success');
      },
      error: (err: any) => {
        this.ngxLoader.stopLoader('loader-01');
        this.toastr.error(err.message);
      }
    });
  }

  limpiarCarga(): void {
    this.fileInput.nativeElement.value = '';
    this.selected = undefined;
  }

  validarExtension(filename: string): boolean {
    const ext = filename.split('.');
    return this.extensionesValidas.includes(ext[ext.length-1]);
  }


  /*
    Evidencias
  */

  obtenerEvidencias(): void {
    this.ngxLoader.startLoader('loader-01');
    this.subscription = this.evidenciasService. obtenerEvidencias(this.paramQueja!.numFolio).subscribe({
      next: (response: Evidencia[]) => {
        this.evidencias = response;
        this.ngxLoader.stopLoader('loader-01');
      },
      error: (error) => {
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        this.ngxLoader.stopLoader('loader-01');
      }
    });
  }

  /*
    Eventos
  */

  onFileDropped(event: any): void {
    this.selected = event[0].file;
    this.cargarArchivo();
  }

  onFileSelected(event: any) {
    this.selected = event.target.files[0];
    this.cargarArchivo();
  }

  onCloseModal(): void {
    this.limpiarCarga();
    this.activeModal.close();
  }
}
