package org.gestoriarr.appgestoriarr.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

public abstract class FirebaseRepositoryImpl<T extends Identificable> implements FirebaseRepository<T> {
	
	@Autowired
	private Firestore firestore;
	
	private final Class<T> entityClass;
	private final String collection;
	
	protected FirebaseRepositoryImpl(Class<T> entityClass, String collection) {
		this.entityClass = entityClass;
		this.collection = collection;
	}

	@Override
	public Optional<T> findById(String id) throws Exception {
		
		DocumentSnapshot doc = firestore.collection(collection)
				.document(id)
				.get()
				.get();
		
		return doc.exists()?Optional.ofNullable(doc.toObject(entityClass)): Optional.empty();
	}

	@Override
	public List<T> findAll() throws Exception {
		
		List<T>lista = new ArrayList<>();
		
		QuerySnapshot query = firestore.collection(collection).get().get();
		
		for(DocumentSnapshot doc : query.getDocuments()) {
			T entity = doc.toObject(entityClass);
			lista.add(entity);
		}
		
		return lista;
	}

	@Override
	public void save(T entity) throws Exception {
		firestore.collection(collection)
			.document(entity.getId())
			.set(entity, SetOptions.merge())
			.get();
	}
	
	

	@Override
	public void deleteById(String id) {
		firestore.collection(collection)
			.document(id)
			.delete();
	}

	@Override
	public List<T> findByField(String field, Object value) throws Exception {
		List<T>lista = new ArrayList<>();
		
		QuerySnapshot query = firestore.collection(collection)
				.whereEqualTo(field, value)
				.get().get();
		
		for (DocumentSnapshot documentSnapshot : query.getDocuments()) {
			lista.add(documentSnapshot.toObject(entityClass));
		}
		
		return lista;
	}
	

}
