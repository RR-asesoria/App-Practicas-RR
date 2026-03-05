package org.gestoriarr.appgestoriarr.controller;

import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.repository.FiltroCliente;
import org.gestoriarr.appgestoriarr.service.ClienteAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteAppController {

    private final ClienteAppService clienteService;

    public ClienteAppController(ClienteAppService clienteService) {
        this.clienteService = clienteService;
    }

    // =========================
    // CREAR CLIENTE
    // =========================
    @PostMapping
    public ResponseEntity<String> crearCliente(@RequestBody ClienteApp cliente) {
        try {
            clienteService.crearCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cliente creado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // =========================
    // OBTENER CLIENTE POR DNI
    // =========================
    @GetMapping("/{nifCif}")
    public ResponseEntity<ClienteApp> obtenerCliente(@PathVariable String nifCif) {
        try {
            ClienteApp cliente = clienteService.obtenerCliente(nifCif);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // =========================
    // OBTENER TODOS LOS CLIENTES
    // =========================
    @GetMapping
    public List<ClienteApp> obtenerTodos() {
        return clienteService.obtenerTodos();
    }

    // =========================
    // ACTUALIZAR CLIENTE
    // =========================
    @PutMapping("/{nifCif}")
    public ResponseEntity<String> actualizarCliente(@PathVariable String nifCif,
                                                    @RequestBody ClienteApp cliente) {
        try {
            cliente.setNifCif(nifCif); // Aseguramos que el DNI sea consistente
            clienteService.actualizarCliente(cliente);
            return ResponseEntity.ok("Cliente actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================
    @DeleteMapping("/{nifCif}")
    public ResponseEntity<String> eliminarCliente(@PathVariable String nifCif) {
        try {
            clienteService.eliminarCliente(nifCif);
            return ResponseEntity.ok("Cliente eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // =========================
    // BUSQUEDA POR FILTROS
    // =========================
    @PostMapping("/buscar")
    public List<ClienteApp> buscarPorFiltros(@RequestBody Map<String, FiltroCliente> filtros) {
        return clienteService.buscarPorFiltros(filtros);
    }

    // =========================
    // BUSQUEDA POR NOMBRE PARCIAL
    // =========================
    @GetMapping("/buscar/nombre")
    public List<ClienteApp> buscarPorNombre(@RequestParam String nombre) {
        return clienteService.buscarPorNombre(nombre);
    }

    // =========================
    // MOVER CASILLA 505
    // =========================
    @PostMapping("/{nifCif}/mover-casilla505")
    public ResponseEntity<String> moverCasilla505(@PathVariable String nifCif) {
        try {
            clienteService.moverCasilla505(nifCif);
            return ResponseEntity.ok("Casilla505 movida correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}