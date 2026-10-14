import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthUser, Empresa } from '../../../core/models';
import { AuthService } from '../../../core/services';

declare var $:any;

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements AfterViewInit { 
  public empresa?: Empresa | null;

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  constructor() {
    this.empresa = this.authService.usuarioAutenticado?.empresa;
  }

  ngAfterViewInit(): void {
    $('#side-menu').metisMenu({
      preventDefault: true,
      toggle: true,
    });
  }

  activeRoute(routeName: string): boolean {
    return this.router.url.indexOf(routeName) > -1;
  }
}
