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

//Crear cliente

    public void crearCliente(ClienteApp cliente) {

        try {

            clientes()
                    .document(cliente.getNifCif())
                    .set(cliente)
                    .get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

//comprobar si un cliente existe

    public boolean existeCliente(String nifCif) {

        try {

            DocumentSnapshot doc =
                    clientes()
                            .document(nifCif)
                            .get()
                            .get();

            return doc.exists();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

   //obtener cliente

    public ClienteApp obtenerCliente(String nifCif) {

        try {

            DocumentSnapshot doc =
                    clientes()
                            .document(nifCif)
                            .get()
                            .get();

            if (!doc.exists()) {
                return null;
            }

            return doc.toObject(ClienteApp.class);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

 //find All

    public List<ClienteApp> obtenerTodos() {

        try {

            QuerySnapshot query = clientes().get().get();

            return query
                    .getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(ClienteApp.class))
                    .toList();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

 //update cliente

    public void actualizarCliente(ClienteApp cliente) {

        try {

            clientes()
                    .document(cliente.getNifCif())
                    .set(cliente, SetOptions.merge())
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

//delete cliente

    public void eliminarCliente(String nifCif) {

        try {

            clientes()
                    .document(nifCif)
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

//busqueda dinamica de filtros

    public List<ClienteApp> buscar(Map<String, Object> filtros) {

        try {

            Query query = clientes();

            for (Map.Entry<String, Object> filtro : filtros.entrySet()) {

                if (filtro.getValue() != null) {

                    query = query.whereEqualTo(
                            filtro.getKey(),
                            filtro.getValue()
                    );

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

//busqueda parcial de nombre

    public List<ClienteApp> buscarPorNombre(String nombre) {

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

// pasar la casilla505

    public void actualizarCasilla505DesdeHistorico(String nifCif) {

        try {

            DocumentSnapshot historico =
                    historicos()
                            .document(nifCif)
                            .get()
                            .get();

            if (!historico.exists()) {
                return;
            }

            String casilla505Actual =
                    historico.getString("casilla505Actual");

            clientes()
                    .document(nifCif)
                    .update(
                            "casilla505anterior",
                            casilla505Actual
                    )
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

}