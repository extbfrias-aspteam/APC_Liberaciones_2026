import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AUTH_USERNAME } from '../../core/constants/auth.constants';
import { CommonModule } from '@angular/common';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { AuthService, CatalogosService } from '../../core/services';
import { ToastrService } from 'ngx-toastr';
import { Empresa, JsonResponse } from '../../core/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit, OnDestroy {
  public empresas: Empresa[] = [];
  public form: FormGroup = new FormGroup({
    username: new FormControl<string>('', [Validators.required]),
    password: new FormControl<string>('', [Validators.required, Validators.minLength(5)]),
    empresa:  new FormControl<string>('', [Validators.required]),
    remember: new FormControl<boolean>(false),
  });
  
  private readonly ngxLoader        = inject(NgxUiLoaderService);
  private readonly authService      = inject(AuthService);
  private readonly toastr           = inject(ToastrService);
  private readonly router           = inject(Router);
  private readonly catalogosService = inject(CatalogosService);
  
  private subscription?: Subscription;
  
  constructor() {}

  /*
    Propiedades
  */

  get getUsername() {
    return this.form.get('username');
  }

  get getPassword() {
    return this.form.get('password');
  }

  get getEmpresa() {
    return this.form.get('empresa');
  }

  get getRemember() {
    return this.form.get('remember');
  }

  ngOnInit(): void {
    if (localStorage.getItem(AUTH_USERNAME)) {
      this.form.setValue({
        username: localStorage.getItem(AUTH_USERNAME),
        password: '',
        empresa: '',
        remember: true
      });
      this.form.controls['username'].markAsTouched();
    }
    this.obtenerEmpresas();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  onLogin(): void {   
    this.ngxLoader.start();
    this.subscription = this.authService.autenticar(this.getUsername?.value, this.getPassword?.value, this.getEmpresa?.value).subscribe({
      next: (result: JsonResponse) => {
        this.ngxLoader.stop();
        if (result.status == 'success') {
          if (this.getRemember?.value) {
            localStorage.setItem(AUTH_USERNAME, this.getUsername?.value);
          } else {
            localStorage.removeItem(AUTH_USERNAME);
          }
          this.authService.guardarToken(result.data);
          this.router.navigate(['/redeco/consultar']).then((navegacionExitosa) => {
            if(!navegacionExitosa){
              this.toastr.error('El token es inválido o ha expirado');
            }
          }).catch(err => {
            console.error('Error en la navegación:', err);
          });
        } else {
          this.toastr.error(result.message);
        }
      },
      error: (err) => {
        this.ngxLoader.stop();
        this.toastr.error(`Hubo un problema a intentar obtener los datos de sesión: ${err.message}`);
      }
    });
  }

  obtenerEmpresas(): void {
    this.ngxLoader.start();
    this.subscription = this.catalogosService.obtenerEmpresas().subscribe({
      next: (response: Empresa[]) => {
        this.empresas = response;
        this.ngxLoader.stop();
      },
      error: (err) => {
        this.ngxLoader.stop();
        this.toastr.error(`Hubo un problema a intentar obtener los datos de sesión: ${err.message}`);
      }
    });
  }
}
