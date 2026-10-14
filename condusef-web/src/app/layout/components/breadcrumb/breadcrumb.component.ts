import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Breadcrumb {
  label: string;
  url: string;
}

@Component({
  selector: 'app-breadcrumb',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit {
  breadcrumbs: Breadcrumb[] = [];
  currentTitle: string | undefined;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.restoreState(); // Restaurar estado al inicializar el componente

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => {
        const root = this.activatedRoute.root;
        const title = this.extractTitle(root);
        this.currentTitle = title;
        return this.buildBreadcrumb(root);
      })
    ).subscribe(breadcrumbs => {
      this.breadcrumbs = breadcrumbs;
      this.saveState(); // Guardar estado después de cada navegación
    });
  }

  private extractTitle(route: ActivatedRoute): string | undefined {
    let title: string | undefined;
    while (route.firstChild) {
      route = route.firstChild;
      title = route.snapshot.routeConfig?.title as string | undefined;
    }
    return title;
  }

  private buildBreadcrumb(route: ActivatedRoute, url: string = '', breadcrumbs: Breadcrumb[] = []): Breadcrumb[] {
    const children: ActivatedRoute[] = route.children;

    if (children.length === 0) {
      return breadcrumbs;
    }

    for (const child of children) {
      const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');
      if (routeURL !== '') {
        url += `/${routeURL}`;
      }

      const label = child.snapshot.data['breadcrumb'];
      if (label) {
        breadcrumbs.push({ label, url });
      }

      return this.buildBreadcrumb(child, url, breadcrumbs);
    }

    return breadcrumbs;
  }

  private saveState(): void {
    localStorage.setItem('breadcrumbState', JSON.stringify({ breadcrumbs: this.breadcrumbs, currentTitle: this.currentTitle }));
  }

  private restoreState(): void {
    const state = localStorage.getItem('breadcrumbState');
    if (state) {
      const { breadcrumbs, currentTitle } = JSON.parse(state);
      this.breadcrumbs = breadcrumbs;
      this.currentTitle = currentTitle;
    }
  }
}
