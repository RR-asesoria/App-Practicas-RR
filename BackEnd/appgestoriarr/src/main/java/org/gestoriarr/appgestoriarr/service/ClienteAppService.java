package org.gestoriarr.appgestoriarr.service;

import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.repository.ClienteAppRepo;
import org.gestoriarr.appgestoriarr.repository.FiltroCliente;
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
        if (repo.existsById(cliente.getNifCif())) {
            throw new RuntimeException("El cliente ya existe");
        }
        repo.save(cliente);
    }

    // =========================
    // OBTENER CLIENTE
    // =========================
    public ClienteApp obtenerCliente(String nifCif) {
        ClienteApp cliente = repo.findById(nifCif);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }
        return cliente;
    }

    // =========================
    // OBTENER TODOS
    // =========================
    public List<ClienteApp> obtenerTodos() {
        return repo.findAll();
    }

    // =========================
    // ACTUALIZAR CLIENTE
    // =========================
    public void actualizarCliente(ClienteApp cliente) {
        if (!repo.existsById(cliente.getNifCif())) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.update(cliente);
    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================
    public void eliminarCliente(String nifCif) {
        if (!repo.existsById(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.deleteById(nifCif);
    }

    // =========================
    // BUSQUEDA DINAMICA (por filtros)
    // =========================
    public List<ClienteApp> buscarPorFiltros(Map<String, FiltroCliente> filtros) {
        return repo.findByFilters(filtros);
    }

    // =========================
    // BUSCAR POR NOMBRE PARCIAL
    // =========================
    public List<ClienteApp> buscarPorNombre(String nombre) {
        return repo.findByNombreContaining(nombre);
    }

    // =========================
    // COPIAR/MOVER CASILLA 505
    // =========================
    public void moverCasilla505(String nifCif) {
        if (!repo.existsById(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.moverCasilla505(nifCif);
    }
}