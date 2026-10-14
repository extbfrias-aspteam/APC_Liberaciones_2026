import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { smoothlyMenu } from '../../../app.helpers';
import { BreadcrumbComponent } from '../breadcrumb/breadcrumb.component';
import { AuthService } from '../../../core/services';
import { AuthUser } from '../../../core/models';

declare var $: any;

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, BreadcrumbComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  public usuarioAutenticado?: AuthUser | null;
  
  private readonly authService = inject(AuthService);

  constructor() {
    this.usuarioAutenticado = this.authService.usuarioAutenticado;
  }

  logout(): void {
    this.authService.cerrarSesion();
  }

  toggleNavigation(): void {
    $('body').toggleClass('mini-navbar');
    smoothlyMenu();
  }
}
