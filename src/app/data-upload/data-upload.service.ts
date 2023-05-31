import { Injectable } from "@angular/core";
import { catchError } from "rxjs";

import { HttpClient, HttpErrorResponse, HttpHandler, HttpHeaders } from '@angular/common/http';



@Injectable({providedIn: 'root'})
export class DataUploadService{

    
    constructor(private httpClient: HttpClient){}

    

    public getDbLists(){
        
        return this.httpClient.get('http://localhost:8010/db')

    }


    public uploadfile(file: File,dbName:String) {
        let formParams = new FormData();
        formParams.append('file', file)
        return this.httpClient.post(`http://localhost:8010/UploadExcelSheet/${dbName}`, formParams)
      }
}
