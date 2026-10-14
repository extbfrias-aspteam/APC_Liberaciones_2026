import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormArray,  FormGroup, ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { ReuneService } from '../../../core/services';
import { Periodo } from '../../../core/models';
import { NgbActiveModal, NgbModal, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Registro, ResponseCondusef } from '../../../core/models/registro.model';
import { CondusefApiResponseComponent } from '../../shared/condusef/condusef-api-response/condusef-api-response.component';

@Component({
  selector: 'app-reune-report-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgbNavModule, NgxUiLoaderModule],
  templateUrl: './reune-report-modal.component.html',
  styleUrl: './reune-report-modal.component.scss'
})
export class ReuneReportModalComponent implements OnInit, OnDestroy {
  @Input() paramPeriodo: Periodo | undefined;
  
  tabActivo: number = 1;
  procesando: boolean = false;
  errorMessage: string = '';
  registros: Registro[] = [];
  registrosReportados = [];
  registrosReportadosError  = [];

  form: FormGroup;
  

  private subscription?: Subscription;
  
  public activeModal       = inject(NgbActiveModal);
  private toastr           = inject(ToastrService);
  private reuneService    = inject(ReuneService);
  private ngxLoader        = inject(NgxUiLoaderService);
  private modalService = inject(NgbModal);
  
  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      folios: this.fb.array([], [Validators.required]),
    })
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  /** 
   * Métodos
  */
  public reportarRegistros(): void {
    this.ngxLoader.startLoader("loader-01");
    this.procesando = true;

    this.subscription = this.reuneService.reportarRegistros(this.form.value).subscribe({
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
  onCheckboxChange(e: any) {
    const folios: FormArray = this.form.get('folios') as FormArray;

    if (e.target.checked) {
      folios.push(new FormControl(e.target.value));
    } else {
      let i: number = 0;
      folios.controls.forEach((item: any) => {
        if (item.value == e.target.value) {
          folios.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

}
