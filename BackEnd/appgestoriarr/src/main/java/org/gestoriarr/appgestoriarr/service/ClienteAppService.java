package org.gestoriarr.appgestoriarr.service;

import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.repository.ClienteAppRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClienteAppService {

    private final ClienteAppRepo repo;

    public ClienteAppService(ClienteAppRepo repo) {
        this.repo = repo;
    }

    // =========================
    // CREAR CLIENTE
    // =========================

    public void crearCliente(ClienteApp cliente) {

        if (repo.existeCliente(cliente.getNifCif())) {
            throw new RuntimeException("El cliente ya existe");
        }

        repo.crearCliente(cliente);
    }

    // =========================
    // OBTENER CLIENTE
    // =========================

    public ClienteApp obtenerCliente(String nifCif) {

        ClienteApp cliente = repo.obtenerCliente(nifCif);

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        return cliente;
    }

    // =========================
    // OBTENER TODOS
    // =========================

    public List<ClienteApp> obtenerTodos() {

        return repo.obtenerTodos();

    }

    // =========================
    // ACTUALIZAR CLIENTE
    // =========================

    public void actualizarCliente(ClienteApp cliente) {

        if (!repo.existeCliente(cliente.getNifCif())) {
            throw new RuntimeException("El cliente no existe");
        }

        repo.actualizarCliente(cliente);

    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================

    public void eliminarCliente(String nifCif) {

        if (!repo.existeCliente(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }

        repo.eliminarCliente(nifCif);

    }

    // =========================
    // BUSQUEDA DINAMICA
    // =========================

    public List<ClienteApp> buscar(Map<String, Object> filtros) {

        return repo.buscar(filtros);

    }

    // =========================
    // BUSCAR POR NOMBRE
    // =========================

    public List<ClienteApp> buscarPorNombre(String nombre) {

        return repo.buscarPorNombre(nombre);

    }

    // =========================
    // COPIAR CASILLA 505
    // =========================

    public void actualizarCasilla505DesdeHistorico(String nifCif) {

        if (!repo.existeCliente(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }

        repo.actualizarCasilla505DesdeHistorico(nifCif);

    }

}