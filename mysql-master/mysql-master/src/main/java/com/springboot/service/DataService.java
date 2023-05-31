package com.springboot.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

	@Autowired
	EntityManager entityManager;
	
	@Transactional
	public void persistEntity(Object object) {
		entityManager.persist(object);
	}
}
