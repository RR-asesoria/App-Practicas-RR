package org.gestoriarr.appgestoriarr.service;


import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.gestoriarr.appgestoriarr.repository.ClienteAppHistoricoRepo;
import org.gestoriarr.appgestoriarr.repository.FiltroCliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClienteAppHistoricoService {

    private final ClienteAppHistoricoRepo repo;

    public ClienteAppHistoricoService(ClienteAppHistoricoRepo repo) {
        this.repo = repo;
    }

    public ClienteAppHistorico obtenerCliente(String nifCif) {
        ClienteAppHistorico clienteAppHistorico = repo.findById(nifCif);
        if (clienteAppHistorico == null) {
            throw new RuntimeException("El cliente no existe");
        }
        return clienteAppHistorico;
    }
    public List<ClienteAppHistorico> obtenerTodosClientes() {
        return repo.findAll();
    }

    public void actualizarCliente(ClienteAppHistorico clienteAppHistorico) {
        if (!repo.existsById(clienteAppHistorico.getNifCif())) {
            throw new RuntimeException("El cliente ya existe");
        }
        repo.update(clienteAppHistorico);
    }

    public void eliminarCliente(String nifCif) {
        if (!repo.existsById(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.deleteById(nifCif);
    }

    public List<ClienteAppHistorico> buscarPorFiltros(Map<String, FiltroCliente> filtros) {
        return repo.findByFilters(filtros);
    }

    public List<ClienteAppHistorico> buscarPorNombre(String nombre) {
        return repo.findByNombreContaining(nombre);
    }
}
