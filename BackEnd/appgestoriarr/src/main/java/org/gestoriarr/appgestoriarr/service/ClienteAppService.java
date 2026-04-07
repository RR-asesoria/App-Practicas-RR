package org.gestoriarr.appgestoriarr.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteBatch;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.gestoriarr.appgestoriarr.model.enums.*;
import org.gestoriarr.appgestoriarr.repository.ClienteAppRepo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClienteAppService {

    private final ClienteAppRepo repo;

    public ClienteAppService(ClienteAppRepo repo) {
        this.repo = repo;
    }

    // CREAR CLIENTE
    public void crearCliente(ClienteApp cliente) {
        if (repo.existsById(cliente.getNifCif())) {
            throw new RuntimeException("El cliente ya existe");
        }
        repo.save(cliente);
    }

    // OBTENER CLIENTE
    public ClienteApp obtenerCliente(String nifCif) {
        ClienteApp cliente = repo.findById(nifCif);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }
        return cliente;
    }

    // OBTENER TODOS
    public List<ClienteApp> obtenerTodos() {
        return repo.findAll();
    }

    // ACTUALIZAR CLIENTE
    public void actualizarCliente(ClienteApp cliente) {
        if (!repo.existsById(cliente.getNifCif())) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.update(cliente);
    }

    // ELIMINAR CLIENTE
    public void eliminarCliente(String nifCif) {
        if (!repo.existsById(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.deleteById(nifCif);
    }

    public List<ClienteApp> buscarPorFiltros(Map<String, Object> filtros) {
        // Limpiamos filtros nulos para que no rompa Firestore
        Map<String, Object> filtrosNoNulos = filtros.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return repo.findByFilters(filtrosNoNulos);
    }
    // BUSCAR POR NOMBRE PARCIAL
    public List<ClienteApp> buscarPorNombre(String nombre) {
        return repo.findByNombreContaining(nombre);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    public void cierreEjercicio() {

        List<ClienteApp> clientes = repo.findAll();
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);

        WriteBatch batch = repo.getDb().batch();
        int contador = 0;

        for (ClienteApp cliente : clientes) {

            // Referencia cliente
            DocumentReference clienteRef = repo.getDb()
                    .collection("ClienteApp")
                    .document(cliente.getNifCif());

            // Referencia histórico
            DocumentReference historicoRef = repo.getDb()
                    .collection("ClienteAppHistorico")
                    .document();

            // HISTÓRICO

            ClienteAppHistorico historico = ClienteAppHistorico.builder()
                    .nifCif(cliente.getNifCif())
                    .nombre(cliente.getNombre())
                    .telefono(cliente.getTelefono())
                    .correoElectronico(cliente.getCorreoElectronico())
                    .fechaNacimiento(cliente.getFechaNacimiento() != null
                            ? new java.sql.Date(cliente.getFechaNacimiento().getTime())
                            : null)
                    .referencia(cliente.getReferencia())
                    .numerosCC(cliente.getNumerosCC())
                    .datosFiscalesDescargados(cliente.isDatosFiscalesDescargados())
                    .importe(cliente.getImporte())
                    .tipoFacturado(cliente.getTipoFacturado())
                    .recogidaDatos(cliente.getRecogidaDatos())
                    .excelDatosElaboracion(cliente.isExcelDatosElaboracion())
                    .borrador(cliente.getBorrador())
                    .presentada(cliente.getPresentada())
                    .cobrado(cliente.getCobrado())
                    .tipoCliente(cliente.getTipoCliente())
                    .estadoCliente(cliente.getEstadoCliente())
                    .casilla505Actual(cliente.getCasilla505Actual())
                    .anioFiscal(String.valueOf(anioActual))
                    .nifAnterior(cliente.getNifAnterior())
                    .build();

            batch.set(historicoRef, historico);


            // UPDATE CLIENTE

            Map<String, Object> updates = new HashMap<>();

            // movimiento casilla
            updates.put("casilla505anterior", cliente.getCasilla505Actual());
            updates.put("casilla505Actual", null);

            // defaults
            updates.put("datosFiscalesDescargados", false);
            updates.put("importe", "0");
            updates.put("tipoFacturado", TipoFacturado.FACTURADONO);
            updates.put("recogidaDatos", TipoRecogidaDatos.FACTURARELLENANO);
            updates.put("excelDatosElaboracion", false);
            updates.put("borrador", TipoBorrador.BORRADORCREADONO);
            updates.put("presentada", TipoPresentada.CONFIRMADOPRESENTARNO);
            updates.put("cobrado", "NO");
            updates.put("estadoCliente", EstadoCliente.CONTACTADONO);

            batch.update(clienteRef, updates);

            contador+= 2;

            // límite Firestore
            if (contador >= 500) {
                try {
                    batch.commit().get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                batch = repo.getDb().batch();
                contador = 0;
            }
        }

        // Commit final
        if (contador > 0) {
            try {
                batch.commit().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void eliminarTodos() {
        List<ClienteApp> clientes = repo.findAll();
        clientes.forEach(c -> repo.deleteById(c.getNifCif()));
    }

    //cambiar dni
    public void cambiarNif(String nifViejo, String nifNuevo) {
        if (!repo.existsById(nifViejo)) {
            throw new RuntimeException("El cliente con NIF " + nifViejo + " no existe");
        }
        if (repo.existsById(nifNuevo)) {
            throw new RuntimeException("Ya existe un cliente con NIF " + nifNuevo);
        }
        repo.cambiarNif(nifViejo, nifNuevo);
    }

    public void actualizarDatosBasicos(ClienteApp cliente) {
        if (!repo.existsById(cliente.getNifCif())) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.actualizarDatosBasicos(cliente);
    }

}