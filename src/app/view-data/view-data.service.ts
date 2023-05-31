import { Injectable } from "@angular/core";
import { catchError } from "rxjs";

import { HttpClient, HttpErrorResponse, HttpHandler, HttpHeaders } from '@angular/common/http';



@Injectable({providedIn: 'root'})
export class ViewDataService{

    
    constructor(private httpClient: HttpClient){}

    

    public getDbLists(){
        return this.httpClient.get('http://localhost:8010/db')
    }

    public getDataFromDb(dbName){
        return this.httpClient.get(`http://localhost:8010/all/${dbName}`)
    }


}
