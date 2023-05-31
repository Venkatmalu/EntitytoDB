package com.springboot.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class DynamciJpaRepository {
	@PersistenceContext
	private EntityManager entityManager;
	
	public void saveDynamicEntity(Object dynamicEntity) {
		entityManager.persist(dynamicEntity);
	}
	

}
