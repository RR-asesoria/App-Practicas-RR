package org.gestoriarr.appgestoriarr.repository;

import com.google.cloud.firestore.*;
import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class ClienteAppHistoricoRepo {

    private final Firestore db;

    public ClienteAppHistoricoRepo(Firestore db) {
        this.db = db;
    }

    private CollectionReference historicos() {
        return db.collection("ClienteAppHistorico");
    }

    public void update(ClienteAppHistorico clienteAppHistorico) {
        try {
            historicos().document(clienteAppHistorico.getNifCif()).set(clienteAppHistorico, SetOptions.merge()).get();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(String nifCif) {
        try {
            historicos().document(nifCif).delete().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(String nifCif) {
        try {
            DocumentSnapshot doc = historicos().document(nifCif).get().get();
            return doc.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ClienteAppHistorico findById(String nifCif) {
        try {
            DocumentSnapshot doc = historicos().document(nifCif).get().get();
            if (!doc.exists()) return null;
            return doc.toObject(ClienteAppHistorico.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ClienteAppHistorico> findAll() {
        try {
            QuerySnapshot query = historicos().get().get();
            List<ClienteAppHistorico> lista = new ArrayList<>();
            for (DocumentSnapshot doc : query.getDocuments()) {
                lista.add(doc.toObject(ClienteAppHistorico.class));
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<ClienteAppHistorico> findByNombreContaining(String nombre) {
        try {
            Query query = historicos().orderBy("nombre").startAt(nombre).endAt(nombre + "\uf8ff");

            QuerySnapshot resultado = query.get().get();
            List<ClienteAppHistorico> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteAppHistorico.class));
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ClienteAppHistorico> findByFilters(Map<String, Object> filtros) {
        try {
            Query query = historicos(); // referencia a la colección Firestore

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
            List<ClienteAppHistorico> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteAppHistorico.class));
            }
            return lista;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error buscando clientes", e);
        }
    }
}
