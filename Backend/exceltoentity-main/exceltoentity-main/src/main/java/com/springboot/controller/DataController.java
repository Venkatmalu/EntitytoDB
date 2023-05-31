package com.springboot.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.springboot.entity.ExcelData;
import com.springboot.service.DataService;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class DataController {
	
	@Autowired
	private DataService dataService;
	
	
	//Saving excel data to db
	@PostMapping(path = "/UploadExcelSheet/{dbName}")
	public ResponseEntity<?> importcsvtodb(@RequestParam("file") MultipartFile multipartFile,@PathVariable String dbName) throws IOException {
	    InputStream stream = multipartFile.getInputStream();
	    
		List<ExcelData> excelData = Poiji.fromExcel(stream, PoijiExcelType.XLSX, ExcelData.class);
		dataService.saveData(excelData,dbName);
    	return ResponseEntity.ok(Map.of("Message", "Data Stored Successfully!!!"));	
}
	
	@GetMapping("/all/{dbName}")
	public ResponseEntity<List<JsonObject>> retirevEntitydata(@PathVariable String dbName){
		
		return new ResponseEntity<>(dataService.retrieveAll(dbName),HttpStatus.OK);
	}
	
	@GetMapping("/db")
	public ResponseEntity<Set<String>> retirevDbCollections(){
		return new ResponseEntity<>(dataService.retrieveAllDb(),HttpStatus.OK);
	}
	

}
