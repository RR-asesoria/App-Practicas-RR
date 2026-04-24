package org.gestoriarr.appgestoriarr.repository;

import com.google.cloud.firestore.*;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
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

    public void deleteByNifCif(String nifCif) {
        try {
            Query query = historicos().whereEqualTo("nifCif", nifCif);
            QuerySnapshot result = query.get().get();

            if (result.isEmpty()) {
                System.out.println("No se encontró ningún cliente con nifCif: " + nifCif);
                return;
            }

            for (DocumentSnapshot document : result.getDocuments()) {
                document.getReference().delete().get();
            }

            System.out.println("Cliente(s) eliminado(s) con nifCif: " + nifCif);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        try {
            QuerySnapshot querySnapshot = historicos().get().get();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                document.getReference().delete().get();
            }

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

    public ClienteAppHistorico findByNifCif(String nifCif) {
        try {
            Query query = historicos().whereEqualTo("nifCif", nifCif);
            QuerySnapshot result = query.get().get();

            if (result.isEmpty()) return null;

            return result.getDocuments().get(0).toObject(ClienteAppHistorico.class);

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

            List<ClienteAppHistorico> todos = findAll();
            String nombreLower = nombre.toLowerCase();
            return todos.stream().filter(c -> c.getNombre() != null && c.getNombre().toLowerCase().contains(nombreLower)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ClienteAppHistorico> findByFilters(Map<String, Object> filtros) {
        try {
            String nombreFiltro = null;
            Map<String, Object> filtrosSinNombre = new HashMap<>(filtros);

            if (filtros.containsKey("nombre")) {
                nombreFiltro = filtros.get("nombre").toString().toLowerCase();
                filtrosSinNombre.remove("nombre");
            }

            Query query = historicos();

            for (Map.Entry<String, Object> entry : filtrosSinNombre.entrySet()) {
                String campo = entry.getKey();
                Object valor = entry.getValue();

                if (valor != null) {
                    if ("fechaNacimiento".equals(campo) && valor instanceof String) {
                        String fechaStr = ((String) valor).trim();
                        try {
                            Instant instant = Instant.parse(fechaStr);
                            Date fecha = Date.from(instant);
                            query = query.whereEqualTo(campo, fecha);
                        } catch (DateTimeParseException e) {
                            throw new RuntimeException("Formato de fecha inválido: " + fechaStr, e);
                        }
                    } else {
                        query = query.whereEqualTo(campo, valor);
                    }
                }
            }

            QuerySnapshot resultado = query.get().get();
            List<ClienteAppHistorico> lista = new ArrayList<>();
            for (DocumentSnapshot doc : resultado.getDocuments()) {
                lista.add(doc.toObject(ClienteAppHistorico.class));
            }

            if (nombreFiltro != null) {
                final String nombreFinal = nombreFiltro;
                lista = lista.stream().filter(c -> c.getNombre() != null &&
                                c.getNombre().toLowerCase().contains(nombreFinal))
                        .toList();
            }

            return lista;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error buscando clientes en el historico", e);
        }
    }
}
