package org.gestoriarr.appgestoriarr.repository;

import com.google.cloud.firestore.*;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public List<ClienteAppHistorico> findByFilters(Map<String, FiltroCliente> filtros) {
        try {
            Query query = historicos();

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
            List<ClienteAppHistorico> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteAppHistorico.class));
            }
            return lista;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
