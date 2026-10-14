import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { RedecoService, ReuneService } from '../../../../core/services';
import { AuthuserCONDUSEFResponse } from '../../../../core/models';

@Component({
  selector: 'app-condusef-token-refresh-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgxUiLoaderModule],
  templateUrl: './condusef-token-refresh-modal.component.html',
  styleUrl: './condusef-token-refresh-modal.component.scss'
})
export class CondusefTokenRefreshModalComponent implements OnInit{
  @Input() paramEsquema: string | undefined;

  userForm: FormGroup;
  tokenForm: FormGroup;
  
  public  readonly activeModal   = inject(NgbActiveModal);
  private readonly redecoService = inject(RedecoService);
  private readonly toastr        = inject(ToastrService);
  private readonly ngxLoader     = inject(NgxUiLoaderService);

  constructor() {
    this.userForm = new FormGroup({
      username: new FormControl(null, [Validators.required]),
      password: new FormControl(null, [Validators.required]),
      schema: new FormControl(null, [Validators.required]),
    });

    this.tokenForm = new FormGroup({
      token:    new FormControl(null, [Validators.required]),
    });
  }

  /* 
    Propiedades
  */

  get getUsername() {
    return this.userForm.get('username');
  }

  get getPassword() {
    return this.userForm.get('password');
  }

  get getToken() {
    return this.tokenForm.get('token');
  }

  get getSchema() {
    return this.tokenForm.get('schema');
  }

  /* 
    Métodos
  */

  ngOnInit(): void {
    if (this.paramEsquema) {
      this.userForm.patchValue({
        schema: this.paramEsquema
      });
    }
  }

  iniciarSesionCONDUSEF(): void {
    this.ngxLoader.startLoader("loader-01");
    this.redecoService.iniciarSesionCONDUSEF(this.userForm.value).subscribe({
      next: (response: AuthuserCONDUSEFResponse) => {
        this.ngxLoader.stopLoader("loader-01");
        if (response.msg == '') {
          this.toastr.error(response.message);
          return;
        }

        this.toastr.success(response.msg);
        if (response.user.token_access) {
          this.tokenForm.patchValue({
            token: response.user.token_access,
          });  
        } else {
          this.toastr.error("No fue posible recuperar el token");
        }
      },
      error: (err: any) => {
        this.ngxLoader.stopLoader("loader-01");
        if(err.message){
          this.toastr.error(err.message);
        }else{
          this.toastr.error(err);
        }
        
      }
    });
  }

  guardarToken(): void {
    this.activeModal.close();
  }
}
