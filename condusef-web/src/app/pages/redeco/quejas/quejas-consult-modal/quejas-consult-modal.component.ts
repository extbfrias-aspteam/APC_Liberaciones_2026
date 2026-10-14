import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Subscription } from 'rxjs';
import { Periodo, QuejaReportadaItem } from '../../../../core/models';
import { QuejasService, RedecoService } from '../../../../core/services';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-quejas-consult-modal',
  standalone: true,
  imports: [CommonModule, NgxUiLoaderModule],
  templateUrl: './quejas-consult-modal.component.html',
  styleUrl: './quejas-consult-modal.component.scss'
})
export class QuejasConsultModalComponent implements OnInit, OnDestroy {

  @Input() paramPeriodo: Periodo | undefined;
    
  private readonly ngxLoader     = inject(NgxUiLoaderService);
  public  readonly activeModal   = inject(NgbActiveModal);
  private readonly quejasService = inject(QuejasService);
  private readonly toastr        = inject(ToastrService);

  private subscription?: Subscription;

  quejas: any[];

  constructor() { 
    this.quejas  = [];
  }

  ngOnInit(): void {
    if (this.paramPeriodo) {
      this.obtenerQuejasReportadas();
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

  private obtenerQuejasReportadas(): void {
    this.ngxLoader.startLoader("loader-01");
    this.subscription = this.quejasService.obtenerQuejasReportadasCondusef(this.paramPeriodo!.mes, this.paramPeriodo!.anio).subscribe({
        next: (response: QuejaReportadaItem[]) => {
          this.quejas = response;
          this.ngxLoader.stopLoader("loader-01");
        },
        error: (error) => {
          this.ngxLoader.stopLoader("loader-01");
          if (error.msg) this.toastr.error(`${error.msg}`, 'Ha ocurrido un error...');
          else this.toastr.error(`${error}`, 'Ha ocurrido un error...');
        }
      });
  }
}
