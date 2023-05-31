/*package com.springboot.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages  = "com.springboot")
@EnableTransactionManagement
public class DynamicRepoConfiguration {

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	JpaVendorAdapter jpaVendorAdapter;
	
	@Bean
	public EntityManager entityManager() {
		return entityManagerFactory.createEntityManager();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager=new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager();
	}
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
}
*/