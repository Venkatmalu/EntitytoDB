import { NgModule } from "@angular/core";
import { Routes,RouterModule } from "@angular/router";
import { ViewDataComponent } from './view-data/view-data.component';
import { DataUploadComponent } from './data-upload/data-upload.component';

const appRoutes:Routes=[
    {path:'',redirectTo:'/upload',pathMatch:'full'},
    {path:'upload',component:DataUploadComponent},
    {path:"retrieve",component:ViewDataComponent}
];

@NgModule({
    imports:[RouterModule.forRoot(appRoutes)],
    exports:[RouterModule]
})
export class AppRoutingModule{



}