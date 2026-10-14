import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { QuejasService } from '../../../../core/services';
import { Periodo, QuejaREDECO } from '../../../../core/models';
import { NgbActiveModal, NgbNavModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { FileSaverModule, FileSaverService } from 'ngx-filesaver';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { QuejaEnvioResponse, QuejaEnvioResponseError, QuejaError } from '../../../../core/models/quejaREDECO.model';
import { CondusefApiResponseComponent } from '../../../shared/condusef/condusef-api-response/condusef-api-response.component';
import { ResponseCondusef } from '../../../../core/models/registro.model';

@Component({
  selector: 'app-quejas-report-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgbNavModule, NgxUiLoaderModule, FileSaverModule],
  templateUrl: './quejas-report-modal.component.html',
  styleUrl: './quejas-report-modal.component.scss'
})
export class QuejasReportModalComponent implements OnInit, OnDestroy {
  @Input() paramPeriodo: Periodo | undefined;
  
  tabActivo: number = 1;
  procesando: boolean = false;
  errorMessage: string = '';
  quejas: QuejaREDECO[] = [];
  quejasError: QuejaError[] = [];
  
  form: FormGroup = new FormGroup({
    jfile: new FormControl<string>('', [Validators.required]),
  });

  private subscription?: Subscription;
  
  public activeModal       = inject(NgbActiveModal);
  private toastr           = inject(ToastrService);
  private quejasService    = inject(QuejasService);
  private ngxLoader        = inject(NgxUiLoaderService);
  private fileSaverService = inject(FileSaverService);
  private modalService = inject(NgbModal);
  
  constructor() { }

  ngOnInit(): void {
    if (this.paramPeriodo) {
      this.obtenerQuejasSinReportar();
    } else {
      this.toastr.error('No existe un periodo válido para consultar');
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  /** 
   * Métodos
  */

  private obtenerQuejasSinReportar(): void {
    this.subscription = this.quejasService.obtenerQuejasSinReportar(this.paramPeriodo!.mes).subscribe({
        next: (response: QuejaREDECO[]) => {
          this.quejas = response;
          this.form.patchValue({
            jfile: JSON.stringify(this.quejas, null, 2)
          });
        },
        error: (error) => {
          this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }

  public reportarQuejas(): void {
    this.ngxLoader.startLoader("loader-01");
    this.procesando = true;

    const folios = this.quejas.map(q => q.QuejasFolio);

    this.subscription = this.quejasService.reportarQuejasCondusef({folios: folios}).subscribe({
      next: (response: ResponseCondusef) => {
        this.procesando = false;
        this.ngxLoader.stopLoader("loader-01");
        this.modalService.dismissAll();
        const modalRef = this.modalService.open(CondusefApiResponseComponent, { backdrop: 'static', size: 'lg' });
        modalRef.componentInstance.apiResponse = response;
      },
      error: (error) => {
        this.procesando = false;
        this.ngxLoader.stopLoader("loader-01");
        this.toastr.error(`${error}`, 'Ha ocurrido un error...');
      }
    });
  }

  public guardarComoJSON(): void {
    // exporta la lista de quejas a formato json
    this.fileSaverService.saveText(
      JSON.stringify(this.quejas, null, 2), 'quejas.json'
    );
  }

  public cerrar(): void {
    if (this.quejasError.length > 0) {
      this.activeModal.dismiss();
    } else {
      this.activeModal.close();
    }
  }
}
