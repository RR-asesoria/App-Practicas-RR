package org.gestoriarr.appgestoriarr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.gestoriarr.appgestoriarr.model.ClienteAppHistorico;
import org.gestoriarr.appgestoriarr.service.ClienteAppHistoricoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Obtener cliente", description = "Obtener un cliente mediante su Nif/Cif")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente Encontrado en el historico", content = @Content(schema = @Schema(implementation = ClienteAppHistorico.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no Encontrado en el historico", content = @Content)
    })
    @GetMapping("/buscarcliente/{nifCif}")
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    public ResponseEntity<ClienteAppHistorico> obtenerCliente(@PathVariable String nifCif) {
        try {
            ClienteAppHistorico clienteAppHistorico = clienteHistoricoService.obtenerCliente(nifCif);
            return ResponseEntity.ok().body(clienteAppHistorico);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Obtener todos los clientes", description = "Devuelve la lista completa de clientes en el historico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historico de clientes", content =  @Content(schema = @Schema(implementation = ClienteAppHistorico.class))),
            @ApiResponse(responseCode = "404", description = "Historico de clientes no encontrado", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    public List<ClienteAppHistorico> obtenerClientes() {
        return clienteHistoricoService.obtenerTodosClientes();
    }

    //TESTING
    @Operation(summary = "Eliminar cliente historico (Testing)", description = "Elimina un cliente del historico por su NIF/CIF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente del historico"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    @DeleteMapping("/borrarcliente/{nifCif}")
    public ResponseEntity<String> eliminarCliente(@Parameter(description = "NIF/CIF del cliente a eliminar", required = true) @PathVariable String nifCif) {
        try {
            clienteHistoricoService.eliminarCliente(nifCif);
            return ResponseEntity.ok("Cliente eliminado correctamente");
        } catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    //TESTING
    @Operation(summary = "Elimina todos los clientes del historico", description = "Elimina todos los clientes del historico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes eliminados correctamente del historico"),
            @ApiResponse(responseCode = "404", description = "Error", content = @Content)
    })
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    @DeleteMapping("/eliminartodos")
    public ResponseEntity<String> eliminarTodosClientes() {
        try {
            clienteHistoricoService.eliminarTodosClientes();
             return ResponseEntity.ok("Clientes eliminados correctamente");
        } catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar clientes", description = "Busca clientes en el historico según filtros exactos seleccionados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClienteAppHistorico.class)))),
            @ApiResponse(responseCode = "404",description = "Clientes no encontrados", content = @Content)
    })
    @PostMapping("/buscar")
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    public List<ClienteAppHistorico> buscarPorFiltros(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClienteAppHistorico.class))
            )
            @RequestBody Map<String, Object> filtros) {
        return clienteHistoricoService.buscarPorFiltros(filtros);
    }

    @Operation(summary = "Buscar clientes en el historico por nombre", description = "Busca clientes por coincidencia parcial de nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(schema = @Schema(implementation = ClienteAppHistorico.class)))
    })
    @GetMapping("/buscar/nombre")
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    public List<ClienteAppHistorico> buscarPorNombre(@Parameter(description = "Nombre a buscar", required = true)@RequestParam String nombre) {
        return clienteHistoricoService.buscarPorNombre(nombre);
    }

    /*Puede que crear metodos para aniadir usuarios y usuario*/

    @PutMapping("/actualizar")
    @PreAuthorize("hasAnyRole('USERBASE','ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody ClienteAppHistorico cliente) {
        try {
            clienteHistoricoService.actualizarCliente(cliente);
            return ResponseEntity.ok("Clientes eliminados correctamente");

        }catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/anios")
    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    public ResponseEntity<List<String>> obtenerAniosDisponibles() {
        return ResponseEntity.ok(clienteHistoricoService.obtenerAniosDisponibles());
    }


}
