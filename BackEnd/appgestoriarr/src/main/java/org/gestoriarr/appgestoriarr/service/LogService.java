package org.gestoriarr.appgestoriarr.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;


@Service
public class LogService {

	@Autowired
	private Firestore firestore;

	public void registrarError(String uid, String metodo, String endPoint, String error) {
		Map<String, Object> log = new HashMap<>();
		log.put("tipo","ERROR");
		log.put("uid", uid);
		log.put("metodo", metodo);
		log.put("endpoint", endPoint);
		log.put("error",error);
		log.put("fecha", Instant.now().toString());

		firestore.collection("logs").add(log);
		
	}

}
