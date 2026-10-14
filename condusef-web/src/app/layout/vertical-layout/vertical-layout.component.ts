import { Component, OnInit } from '@angular/core';
import { FooterComponent } from '../components/footer/footer.component';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { HeaderComponent } from '../components/header/header.component';
import { RouterOutlet } from '@angular/router';
import { detectBody } from '../../app.helpers';

@Component({
  selector: 'app-vertical-layout',
  standalone: true,
  imports: [FooterComponent, SidebarComponent, HeaderComponent, RouterOutlet],
  templateUrl: './vertical-layout.component.html',
  styleUrl: './vertical-layout.component.scss'
})
export class VerticalLayoutComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {
    detectBody();
  }

  onResize(): void {
    detectBody();
  }
}
