package org.gestoriarr.appgestoriarr.controller;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.gestoriarr.appgestoriarr.repository.FiltroCliente;
import org.gestoriarr.appgestoriarr.service.ClienteAppHistoricoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientesHistorico")
public class ClienteAppHistoricoController {

    private final ClienteAppHistoricoService clienteHistoricoService;

    public ClienteAppHistoricoController(ClienteAppHistoricoService clienteHistoricoService) {
        this.clienteHistoricoService = clienteHistoricoService;
    }

    @GetMapping("/{nifCif}")
    public ResponseEntity<ClienteAppHistorico> ObtenerCliente(@PathVariable String nifCif) {
        try {
            ClienteAppHistorico clienteAppHistorico = clienteHistoricoService.obtenerCliente(nifCif);
            return ResponseEntity.ok().body(clienteAppHistorico);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public List<ClienteAppHistorico> ObtenerClientes() {
        return clienteHistoricoService.obtenerTodosClientes();
    }

    @DeleteMapping("/{nifCif")
    public ResponseEntity<String> eliminarCliente(@PathVariable String nifCif) {
        try {
            clienteHistoricoService.eliminarCliente(nifCif);
            return ResponseEntity.ok("Cliente eliminado correctamente");
        } catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public List<ClienteAppHistorico> buscarPorFiltros(@RequestBody Map<String, FiltroCliente> filtros) {
        return clienteHistoricoService.buscarPorFiltros(filtros);
    }

    @GetMapping("/buscar/nombre")
    public List<ClienteAppHistorico> buscarPorNombre(@RequestParam String nombre) {
        return clienteHistoricoService.buscarPorNombre(nombre);
    }

}
