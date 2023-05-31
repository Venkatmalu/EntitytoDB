package com.springboot.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.entity.ExcelData;


@Service
public class DataService {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void saveData(List<ExcelData> excelData,String dbName) throws JsonProcessingException {
		
		ObjectMapper objectMapper=new ObjectMapper();
		for(ExcelData data:excelData) {
			Map<?, ?> employeeFields=data.getUnknownCells();
			for (Map.Entry<?, ?> e : employeeFields.entrySet()) {
			  System.out.println(e.getValue().getClass());
			}
			String jsoString= objectMapper.writeValueAsString(employeeFields);
			mongoTemplate.save(jsoString,dbName);
		}		
	}

	public List<JsonObject> retrieveAll(String dbName){
		ProjectionOperation project=project().andExclude("_id");
		Aggregation aggregation=newAggregation(project);		
		AggregationResults<JsonObject> aggregationResults=mongoTemplate.aggregate(aggregation,dbName,JsonObject.class);
		List<JsonObject> results=aggregationResults.getMappedResults();

		return results;
		
	}
	
	public Set<String> retrieveAllDb(){
		return mongoTemplate.getCollectionNames();
	}
	
}
