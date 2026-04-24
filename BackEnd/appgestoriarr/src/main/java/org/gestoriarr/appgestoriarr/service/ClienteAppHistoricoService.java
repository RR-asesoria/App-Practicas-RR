package org.gestoriarr.appgestoriarr.service;


import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.gestoriarr.appgestoriarr.repository.ClienteAppHistoricoRepo;
import org.gestoriarr.appgestoriarr.repository.ClienteAppRepo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClienteAppHistoricoService {

    private final ClienteAppHistoricoRepo repo;

    public ClienteAppHistoricoService(ClienteAppHistoricoRepo repo) {
        this.repo = repo;
    }

    public ClienteAppHistorico obtenerCliente(String nifCif) {
        ClienteAppHistorico clienteAppHistorico = repo.findByNifCif(nifCif);
        if (clienteAppHistorico == null) {
            throw new RuntimeException("El cliente no existe");
        }
        return clienteAppHistorico;
    }
    public List<ClienteAppHistorico> obtenerTodosClientes() {
        return repo.findAll();
    }

    public void eliminarCliente(String nifCif) {
        try {
            repo.deleteById(nifCif);
        } catch (Exception e) {
            throw new RuntimeException("El cliente no existe");
        }
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

    public void actualizarCliente(ClienteAppHistorico clienteAppHistorico) { repo.update(clienteAppHistorico);

    }
    public List<String> obtenerAniosDisponibles() {
        return repo.findAll()
                .stream()
                .map(ClienteAppHistorico::getAnioFiscal)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}
