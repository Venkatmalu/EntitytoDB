/*package com.springboot.repository;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class DynamicRepositoryFactory extends JpaRepositoryFactory{
	
	public DynamicRepositoryFactory(EntityManager entityManager) {
		super(entityManager);
	}
	@Override
	protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,EntityManager entityManager){
		return new SimpleJpaRepository<>(information.getDomainType(), entityManager);
	}
	
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata){
		return DynamicRepository.class;
	}

}
*/