package org.gestoriarr.appgestoriarr.repository;

import java.util.List;
import java.util.Optional;

public interface FirebaseRepository<T>{
	
	Optional<T> findById(String id) throws Exception;
	
	List<T> findAll() throws Exception;
	
	void save(T entity) throws Exception;
	
	void deleteById(String id);
	
	List<T> findByField(String field, Object value) throws Exception;

	
}
