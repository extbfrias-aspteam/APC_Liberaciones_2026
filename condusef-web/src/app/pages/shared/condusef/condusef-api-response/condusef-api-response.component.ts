import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal, NgbAlertModule, NgbAccordionModule, NgbModal, NgbTooltipModule, NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { ApiResponse, ResponseCondusef } from '../../../../core/models/registro.model';
import { AclaracionREUNE, ConsultaREUNE, ReclamacionREUNE, Periodo } from '../../../../core/models';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ReuneService } from '../../../../core/services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-condusef-api-response',
  standalone: true,
  templateUrl: './condusef-api-response.component.html',
  imports: [NgbAccordionModule, NgbAlertModule, NgbTooltipModule, NgbCollapseModule],
  styleUrls: ['./condusef-api-response.component.scss']
})
export class CondusefApiResponseComponent {
  @Input() apiResponse!: ResponseCondusef;
  private ngxLoader      = inject(NgxUiLoaderService);
  private reuneService  = inject(ReuneService);
  public periodo: Periodo | undefined;
  private router         = inject(Router);
  private modalService = inject(NgbModal);
  quejaCollapsed = true;
  consultaCollapsed = true;
  aclaracionCollapsed = true;
  reclamacionCollapsed = true;
  
  getApiResponseKeys(): string[] {
    return Object.keys(this.apiResponse);
  }
  getApiResponseData(key: string): ApiResponse | undefined {
    return this.apiResponse[key];
  }
  getErrorKeys(apiResponse: ApiResponse): string[] {
    return Object.keys(apiResponse.errors);
  }
  getJSONString(data: any): string{
    return JSON.stringify(data);
  }

  navegarEditar(folio: string, tipo: string, errores: string[]): void {
    this.modalService.dismissAll();
    if(tipo == "consultas"){ 
      this.ngxLoader.start();
      this.obtenerConsultaPorFolio(folio).then((registro) => {
        this.ngxLoader.stop();
        this.router.navigate(['/reune/consultas/editar'], 
          { 
            state: {
              'periodo': this.periodo, 
              'editar': true, 
              'folio': folio,
              'registro': registro,
              'errores': errores
            } 
          });
      });
        
    }else if(tipo == "reclamaciones"){ 
      this.ngxLoader.start();
      this.obtenerReclamacionPorFolio(folio).then((registro) => {
        this.ngxLoader.stop();
        this.router.navigate(['/reune/reclamaciones/editar'], 
          { 
            state: {
              'periodo': this.periodo, 
              'editar': true, 
              'folio': folio,
              'registro': registro,
              'errores': errores
            } 
          });
      });
    }else if(tipo == "aclaraciones"){ 
      this.obtenerAclaracionPorFolio(folio).then((registro) => {
        this.ngxLoader.stop();
        this.router.navigate(['/reune/aclaraciones/editar'], 
          { 
            state: {
              'periodo': this.periodo, 
              'editar': true, 
              'folio': folio,
              'registro': registro,
              'errores': errores
            } 
          });
      });
    }
    
  }

  obtenerConsultaPorFolio(folio: string): Promise<ConsultaREUNE> {
    return new Promise(async (resolve) => {
      this.reuneService.obtenerConsultaPorFolio(folio).subscribe((consulta: ConsultaREUNE) => {
        resolve(consulta);
      });    
    });
  }
  obtenerAclaracionPorFolio(folio: string): Promise<AclaracionREUNE> {
      return new Promise(async (resolve) => {
        this.reuneService.obtenerAclaracionPorFolio(folio).subscribe((aclaracion: AclaracionREUNE) => {
          resolve(aclaracion);
        });    
    });
  }
  obtenerReclamacionPorFolio(folio: string): Promise<ReclamacionREUNE> {
    return new Promise(async (resolve) => {
      this.reuneService.obtenerReclamacionPorFolio(folio).subscribe((reclamacion: ReclamacionREUNE) => {
        resolve(reclamacion);
      });    
  });
  }

  constructor(public activeModal: NgbActiveModal) {}
}