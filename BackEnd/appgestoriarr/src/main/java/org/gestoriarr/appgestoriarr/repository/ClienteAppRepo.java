package org.gestoriarr.appgestoriarr.repository;

import com.google.cloud.firestore.*;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.springframework.stereotype.Repository;

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

    // =========================
    // CRUD / estilo Spring Data
    // =========================

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

    // =========================
    // Busqueda parcial por nombre
    // =========================
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

    // =========================
    // Mover casilla505Actual -> casilla505Anterior
    // =========================
    public void moverCasilla505(String nifCif) {
        try {
            DocumentReference clienteRef = clientes().document(nifCif);
            DocumentSnapshot doc = clienteRef.get().get();
            if (!doc.exists()) return;
            String casillaActual = doc.getString("casilla505Actual");

            Map<String,Object> updates = new HashMap<>();
            updates.put("casilla505anterior", casillaActual);
            updates.put("casilla505Actual", null);

            clienteRef.update(updates).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // Buscador dinámico universal
    // =========================
    public List<ClienteApp> findByFilters(Map<String, FiltroCliente> filtros) {
        try {
            Query query = clientes();

            for (Map.Entry<String, FiltroCliente> entry : filtros.entrySet()) {
                String campo = entry.getKey();
                FiltroCliente filtro = entry.getValue();

                if (filtro.getValorIgual() != null) {
                    query = query.whereEqualTo(campo, filtro.getValorIgual());
                }
                if (filtro.getValorMin() != null) {
                    query = query.whereGreaterThanOrEqualTo(campo, filtro.getValorMin());
                }
                if (filtro.getValorMax() != null) {
                    query = query.whereLessThanOrEqualTo(campo, filtro.getValorMax());
                }
                if (filtro.getValorParcial() != null) {
                    query = query
                            .orderBy(campo)
                            .startAt(filtro.getValorParcial())
                            .endAt(filtro.getValorParcial() + "\uf8ff");
                }
            }

            QuerySnapshot resultado = query.get().get();
            List<ClienteApp> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteApp.class));
            }
            return lista;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}