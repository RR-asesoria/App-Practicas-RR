package org.gestoriarr.appgestoriarr.service;


import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.gestoriarr.appgestoriarr.repository.ClienteAppHistoricoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void eliminarCliente(String nifCif) {
        if (!repo.existsById(nifCif)) {
            throw new RuntimeException("El cliente no existe");
        }
        repo.deleteById(nifCif);
    }

    public void eliminarTodosClientes() {
        repo.delete();
    }

    public List<ClienteAppHistorico> buscarPorFiltros(Map<String, Object> filtros) {
        Map<String, Object> filtrosNoNulos = filtros.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return repo.findByFilters(filtrosNoNulos);    }

    public List<ClienteAppHistorico> buscarPorNombre(String nombre) {
        return repo.findByNombreContaining(nombre);
    }
}
