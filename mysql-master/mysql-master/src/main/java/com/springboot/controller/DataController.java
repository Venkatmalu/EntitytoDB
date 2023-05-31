package com.springboot.controller;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.springboot.entity.Employee;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;


@RestController
public class DataController {

	private static void saveValues(List<Employee> employeeList,Class<?> dynamicEntity,String dbName)throws SQLException {
		 String url="jdbc:mysql://localhost:3306/EmployeeDatabase";
		    String username="root";
		    String password="Venkatji@1";
		    Connection connection=DriverManager.getConnection(url,username,password);
		    try {
				for (int i=0;i<employeeList.size();i++) {
					  StringBuilder stringBuilder=new StringBuilder();
					  StringBuilder placeholder=new StringBuilder();

					Object dynamicEnitiObject=dynamicEntity.getDeclaredConstructor().newInstance();
					Employee emp=employeeList.get(i);
					Map<String, String> en=emp.getUnknownCells();
					for(Map.Entry<String,String> entry:en.entrySet()) {
						String fieldnameString=entry.getKey();
						Object valueObject=entry.getValue();
						String setmethodString="set"+capitaliseFirstLetter(fieldnameString);
						dynamicEntity.getMethod(setmethodString,valueObject.getClass()).invoke(dynamicEnitiObject,valueObject);	
					}
					  for(Field field:dynamicEntity.getDeclaredFields()) {
						  
						  field.setAccessible(true);
						  Object valueObject=field.get(dynamicEnitiObject);
						  System.out.println(field.getName()+""+valueObject );
					  }
					  
						 for(Field field:dynamicEntity.getDeclaredFields()) {
							 String fieldName=field.getName();
							  stringBuilder.append(fieldName).append(",");
							  placeholder.append("?,");
					  }
						 stringBuilder.deleteCharAt(stringBuilder.length()-1);
						 placeholder.deleteCharAt(placeholder.length()-1);
						 
						 System.out.println(stringBuilder.toString());
						 System.err.println(placeholder.toString());
						 String tableName=dynamicEntity.getSimpleName();
						 String sqlString=String.format("INSERT INTO %s (%s) VALUES (%s)",dbName,stringBuilder.toString(),placeholder.toString());
						
						 System.out.println(sqlString);
						 
						 try (PreparedStatement statement=connection.prepareStatement(sqlString)){
							 int paramindex=1;
							 
							 for(Field field:dynamicEntity.getDeclaredFields()) {
								 field.setAccessible(true);
								 Object value=field.get(dynamicEnitiObject);
								 statement.setObject(paramindex++, value);		 
						  }
							 statement.executeUpdate();
						} catch (Exception e) {
						}
				}
		}catch (Exception e) {
		}
				connection.close();

	}
	
	
	
	private static String getSqlType(String fieldtype) {
		switch (fieldtype.toLowerCase()) {
		case "long":
			return "bigint";
		case "int":
			return "int";			
		case "string":
			return "varchar(255)";		
		default:
			throw new IllegalArgumentException("unsupported type");
		}
	}
	
	
	
	public static void createTable(Class<?> entityClass,String dbName) throws SQLException{
			String tableName=dbName;
			System.out.println(tableName);
			StringBuilder sqlBuilder=new StringBuilder("CREATE TABLE IF NOT EXISTS ");
			sqlBuilder.append(tableName).append("(");
			
			Field[] fields=entityClass.getDeclaredFields();
			for(Field field: fields) {
				String fileName=field.getName();
				String fieldtype=field.getType().getSimpleName();
				sqlBuilder.append(fileName).append(" ").append(getSqlType(fieldtype)).append(",");			
			}
			sqlBuilder.deleteCharAt(sqlBuilder.length()-1);
			sqlBuilder.append(")");
			System.out.println(sqlBuilder.toString());
			
			 String url="jdbc:mysql://localhost:3306/EmployeeDatabase";
			    String username="root";
			    String password="Venkatji@1";
			  try(  Connection connection=DriverManager.getConnection(url,username,password)){
				  connection.createStatement().execute(sqlBuilder.toString());
			  };
	}
	
	
	
public static  Class<?> generateEntityClass(Map<String, String> data) throws Exception{
	ByteBuddy buddy=new ByteBuddy();
	DynamicType.Builder<?> builder=buddy.subclass(Object.class)
			.modifiers(Visibility.PUBLIC)
			.name("com.springboot.controller.Data")
			.annotateType(AnnotationDescription.Builder.ofType(Entity.class).build())	;	
	    builder =builder.defineField("id",Long.class, Visibility.PRIVATE)
			.annotateField(AnnotationDescription.Builder.ofType(Id.class).build())
			.annotateField(AnnotationDescription.Builder.ofType(GeneratedValue.class).define("strategy", GenerationType.IDENTITY).build())
			.annotateField(AnnotationDescription.Builder.ofType(Column.class).define("name","id").build());
		for(Map.Entry<String,String> entry:data.entrySet()) {
			String fieldNameString=entry.getKey();
			Object fieldValueObject=entry.getValue();			
			String capitalfieldname=capitaliseFirstLetter(fieldNameString);
			String getterString="get"+capitalfieldname;
			String seteString="set"+capitalfieldname;
			Class<?> fieldType=fieldValueObject.getClass();
			builder=builder.defineField(fieldNameString,fieldType,Modifier.PRIVATE)
				.annotateField(AnnotationDescription.Builder.ofType(Column.class).define("name",fieldNameString).build());
			//String capitalizedFiledname=capitaliseFirstLetter(fieldNameString);
			builder=builder.defineMethod(getterString,fieldType,Modifier.PUBLIC)
					.intercept(FieldAccessor.ofField(fieldNameString));
			builder=builder.defineMethod(seteString,Void.TYPE,Modifier.PUBLIC)
					.withParameter(fieldType,fieldNameString)
					.intercept(FieldAccessor.ofField(fieldNameString));
	}
	builder =builder.defineMethod("getId",Long.class,Visibility.PUBLIC)
			.intercept(MethodDelegation.toField("id"));
    //builder.make().saveIn(new File( "C:/Users/2113310/OneDrive - Cognizant/Desktop/New folder (2)/springboot/src/main/java/com/springboot/controller"));
	//builder.make().saveIn(new File( "src/main/java"));
	
	
	DynamicType.Unloaded<?> dynamictyUnloaded=builder.make();
	dynamictyUnloaded.saveIn(new File( "src/main/java"));
	return dynamictyUnloaded.load(DataController.class.getClassLoader(),ClassLoadingStrategy.Default.WRAPPER).getLoaded();
	
	//return builder.make().load(DataController.class.getClassLoader(),ClassLoadingStrategy.Default.WRAPPER).getLoaded();
	}
	private static String capitaliseFirstLetter(String field) {
	if(field==null||field.isEmpty()) {	
		return field;
	}
	return Character.toUpperCase(field.charAt(0))+field.substring(1);
}




@PostMapping(path = "/UploadExcelSheet/{dbName}")
public ResponseEntity<?> importcsvtodb(@RequestParam("file") MultipartFile multipartFile,@PathVariable String dbName ) throws IOException,Exception ,IllegalAccessException,InstantiationException{
	    InputStream stream = multipartFile.getInputStream();
		List<Employee> employeeList = Poiji.fromExcel(stream, PoijiExcelType.XLSX, Employee.class);
		
		Employee data=employeeList.get(0);
		Map<String, String> datahash=data.getUnknownCells();
	
		Class<?> dynamicEntity=generateEntityClass(datahash); 
	try {
		createTable(dynamicEntity,dbName);
	}
	finally {
		System.out.println("Already there");
	}
		saveValues(employeeList,dynamicEntity,dbName);
		
		/*
		for (int i=0;i<employeeList.size();i++) {

			Object dynamicEnitiObject=dynamicEntity.getDeclaredConstructor().newInstance();

			Employee emp=employeeList.get(i);
			Map<String, String> en=emp.getUnknownCells();
			for(Map.Entry<String,String> entry:en.entrySet()) {
				String fieldnameString=entry.getKey();
				Object valueObject=entry.getValue();
				String setmethodString="set"+capitaliseFirstLetter(fieldnameString);
				dynamicEntity.getMethod(setmethodString,valueObject.getClass()).invoke(dynamicEnitiObject,valueObject);	
			    					    
			}	
	
			  for(Field field:dynamicEntity.getDeclaredFields()) {
				  field.setAccessible(true);
				  Object valueObject=field.get(dynamicEnitiObject);
				  System.out.println(field.getName()+""+valueObject );
			  }
		}*/
	

		System.out.println(dynamicEntity.getName());	
		
		return ResponseEntity.ok(Map.of("Message", "Data Stored Successfully!!!"));
	}   
	    
}



























    /*
	    String url="jdbc:mysql://localhost:3306/EmployeeDatabase";
	    String username="root";
	    String password="Arun@2003";
	    Connection connection=DriverManager.getConnection(url,username,password);
	    StringBuilder stringBuilder=new StringBuilder();
	    StringBuilder placeholder=new StringBuilder();
	    

	   
		for (int i=0;i<employeeList.size();i++) {

			Object dynamicEnitiObject=dynamicEntity.getDeclaredConstructor().newInstance();
			Employee emp=employeeList.get(i);
			Map<String, String> en=emp.getUnknownCells();
			for(Map.Entry<String,String> entry:en.entrySet()) {
				String fieldnameString=entry.getKey();
				Object valueObject=entry.getValue();
				String setmethodString="set"+capitaliseFirstLetter(fieldnameString);
				dynamicEntity.getMethod(setmethodString,valueObject.getClass()).invoke(dynamicEnitiObject,valueObject);	
			}
			
			
			  for(Field field:dynamicEntity.getDeclaredFields()) {
				  
				  field.setAccessible(true);
				  Object valueObject=field.get(dynamicEnitiObject);
				  System.out.println(field.getName()+""+valueObject );
			  }
			  
				 for(Field field:dynamicEntity.getDeclaredFields()) {
					 String fieldName=field.getName();
					  stringBuilder.append(fieldName).append(",");
					  placeholder.append("?,");
			  }
				 stringBuilder.deleteCharAt(stringBuilder.length()-1);
				 placeholder.deleteCharAt(placeholder.length()-1);
				 
				 String tableName=dynamicEntity.getSimpleName();
				 String sqlString=String.format("INSERT INTO %s (%s) VALUES (%s)",tableName,stringBuilder.toString(),placeholder.toString());
				 try (PreparedStatement statement=connection.prepareStatement(sqlString)){
					 int paramindex=1;
					 
					 for(Field field:dynamicEntity.getDeclaredFields()) {
						 field.setAccessible(true);
						 Object value=field.get(dynamicEnitiObject);
						 statement.setObject(paramindex++, value);
						 System.out.println(value);
						 System.out.println("new");
								 
				  }
					 statement.executeUpdate();
					 
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			  
		}	
		connection.close();*/
	//	Class<?> enClass=Class.forName("com.springboot.controller.Data");
	/*	MetadataSources metadataSource=new MetadataSources(serviceRegistry);
		metadataSource.addAnnotatedClass(dynamicEntity);
		Metadata metadata=metadataSource.buildMetadata();*/
		
/*		Configuration configuration2=new Configuration();
        configuration2.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/EmployeeDatabase");
        configuration2.setProperty("hibernate.connection.username", "root");
        configuration2.setProperty("hibernate.connection.password", "Arun@2003");
		configuration2.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");*/
	
	  //    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(configuration2);

	 //       EntityManager entityManager = entityManagerFactory.createEntityManager();


	  //      JpaRepository<?, ?> repository = new JpaRepositoryImpl<>(entityManager, dynamicEntity);
	        
			
		//	session.save(date);
	/*		try{
				
				for (int i=0;i<employeeList.size();i++) {

					Object dynamicEnitiObject=dynamicEntity.getDeclaredConstructor().newInstance();

					Employee emp=employeeList.get(i);
					Map<String, String> en=emp.getUnknownCells();
					for(Map.Entry<String,String> entry:en.entrySet()) {
						String fieldnameString=entry.getKey();
						Object valueObject=entry.getValue();
						String setmethodString="set"+capitaliseFirstLetter(fieldnameString);
						dynamicEntity.getMethod(setmethodString,valueObject.getClass()).invoke(dynamicEnitiObject,valueObject);	
					    					    
					}
					session.save(dynamicEnitiObject);

					
					  for(Field field:dynamicEntity.getDeclaredFields()) {
						  field.setAccessible(true);
						  Object valueObject=field.get(dynamicEnitiObject);
						  System.out.println(field.getName()+""+valueObject );
					  }
				}		
				session.getTransaction().commit();
			}
			catch (Exception e) {
				if(transaction!=null) {
					transaction.rollback();
				}
				e.printStackTrace();// TODO: handle exception
			}
			finally {
				session.close();
				sessionFactory.close();
			}*/
			
	    
	    
	    
	    
	    
	    
	    
	    
	    
	        //creating instance and looping to display data
	/*		for (int i=0;i<employeeList.size();i++) {
				Object dynamicEnitiObject=dynamicEntity.getDeclaredConstructor().newInstance();
				Employee emp=employeeList.get(i);
				Map<String, String> en=emp.getUnknownCells();
				for(Map.Entry<String,String> entry:en.entrySet()) {
					String fieldnameString=entry.getKey();
					Object valueObject=entry.getValue();
					String setmethodString="set"+capitaliseFirstLetter(fieldnameString);
					dynamicEntity.getMethod(setmethodString,valueObject.getClass()).invoke(dynamicEnitiObject,valueObject);	
				}
				  for(Field field:dynamicEntity.getDeclaredFields()) {
					  field.setAccessible(true);
					  Object valueObject=field.get(dynamicEnitiObject);
					  System.out.println(field.getName()+""+valueObject );
				  }
			}		
			*/
			
	    
	    
	    
	    
	    
	    
	    
	    
	    

/*
public static FileSystem createJarFile(String jarfilepath) throws IOException{
	Path jarfile=Paths.get(jarfilepath);
	if(Files.exists(jarfile)) {
		Files.delete(jarfile);
	}
	return FileSystems.newFileSystem(jarfile);
}
	*/

/*
public static java.util.jar.Manifest  createJarFile(){
	java.util.jar.Manifest manifest=new java.util.jar.Manifest();
	Attributes attributes=manifest.getMainAttributes();
	attributes.put(Attributes.Name.MANIFEST_VERSION,"1.0");
	return manifest;
	
}*/











