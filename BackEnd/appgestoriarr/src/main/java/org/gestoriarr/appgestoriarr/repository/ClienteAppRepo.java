package org.gestoriarr.appgestoriarr.repository;

import com.google.cloud.firestore.*;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Repository
public class ClienteAppRepo {

    private final Firestore db;

    public ClienteAppRepo(Firestore db) {
        this.db = db;
    }

    private CollectionReference clientes() {
        return db.collection("ClienteApp");
    }

    private CollectionReference historicos() {
        return db.collection("ClienteAppHistorico");
    }


    // CRUD


    public void save(ClienteApp cliente) {
        try {
            clientes().document(cliente.getNifCif()).set(cliente).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(String nifCif) {
        try {
            DocumentSnapshot doc = clientes().document(nifCif).get().get();
            return doc.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ClienteApp findById(String nifCif) {
        try {
            DocumentSnapshot doc = clientes().document(nifCif).get().get();
            if (!doc.exists()) return null;
            return doc.toObject(ClienteApp.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ClienteApp> findAll() {
        try {
            QuerySnapshot query = clientes().get().get();
            List<ClienteApp> lista = new ArrayList<>();
            for (DocumentSnapshot doc : query.getDocuments()) {
                lista.add(doc.toObject(ClienteApp.class));
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(String nifCif) {
        try {
            clientes().document(nifCif).delete().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ClienteApp cliente) {
        try {
            clientes().document(cliente.getNifCif())
                    .set(cliente, SetOptions.merge())
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Busqueda parcial por nombre

    public List<ClienteApp> findByNombreContaining(String nombre) {
        try {
            Query query = clientes()
                    .orderBy("nombre")
                    .startAt(nombre)
                    .endAt(nombre + "\uf8ff");

            QuerySnapshot resultado = query.get().get();
            List<ClienteApp> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteApp.class));
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    public List<ClienteApp> findByFilters(Map<String, Object> filtros) {
        try {
            Query query = clientes(); // referencia a la colección Firestore

            // Aplicar solo filtros no nulos
            for (Map.Entry<String, Object> entry : filtros.entrySet()) {
                String campo = entry.getKey();
                Object valor = entry.getValue();

                if (valor != null) {
                    // Convertimos string ISO a Date si el campo es fecha
                    if ("fechaNacimiento".equals(campo) && valor instanceof String) {
                        String fechaStr = ((String) valor).trim();
                        try {
                            Instant instant = Instant.parse(fechaStr); // parse ISO 8601
                            Date fecha = Date.from(instant);           // convertimos a java.util.Date
                            query = query.whereEqualTo(campo, fecha);
                        } catch (DateTimeParseException e) {
                            throw new RuntimeException("Formato de fecha inválido: " + fechaStr, e);
                        }
                    } else {
                        query = query.whereEqualTo(campo, valor);
                    }
                }
            }

            // Ejecutar query
            QuerySnapshot resultado = query.get().get();
            List<ClienteApp> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteApp.class));
            }
            return lista;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error buscando clientes", e);
        }
    }




    public Firestore getDb() {
        return db;
    }

}