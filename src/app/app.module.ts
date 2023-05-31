import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule}  from '@angular/forms'
import { AppComponent } from './app.component';
import { DataUploadComponent } from './data-upload/data-upload.component';

import {HttpClientModule} from'@angular/common/http';
import { ViewDataComponent } from './view-data/view-data.component';
import { NavbarComponent } from './navbar/navbar.component'
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    DataUploadComponent,
    ViewDataComponent,
    NavbarComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
