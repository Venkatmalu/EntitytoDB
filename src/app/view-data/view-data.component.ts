import { Component, OnDestroy, OnInit } from '@angular/core';
import { ViewDataService } from './view-data.service';
import {Subject } from 'rxjs';

@Component({
  selector: 'app-view-data',
  templateUrl: './view-data.component.html',
  styleUrls: ['./view-data.component.css']
})
export class ViewDataComponent implements OnInit{

  db;

  DbData:any;
  keys:any;

  selectedOption:String;

  

  
  constructor(private viewData:ViewDataService) { }

  ngOnInit(): void {
    this.viewData.getDbLists().subscribe(resp=>{
      this.db=resp;
    })
    
}

onClickSubmit(){
  this.viewData.getDataFromDb(this.selectedOption).subscribe(response=>{
    this.DbData=response;
    this.keys= this.DbData[0]
    console.log(this.DbData)
  })


}

getkeys():String[]{
  let keys:String[]=[];
  this.DbData.array.forEach(items => {
    Object.keys(items).forEach(key=>{
      if(!keys.includes(key)){
        keys.push(key);
      }
    })
    
  });
  return keys;




}}