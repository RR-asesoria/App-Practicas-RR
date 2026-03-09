package org.gestoriarr.appgestoriarr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
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

    @Operation(summary = "Obtener cliente", description = "Obtener un cliente mediante su Nif/Cif")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente Encontrado en el historico", content = @Content(schema = @Schema(implementation = ClienteAppHistorico.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no Encontrado en el historico", content = @Content)
    })
    @GetMapping("/{nifCif}")
    public ResponseEntity<ClienteAppHistorico> ObtenerCliente(@PathVariable String nifCif) {
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
    public List<ClienteAppHistorico> ObtenerClientes() {
        return clienteHistoricoService.obtenerTodosClientes();
    }

    @Operation(summary = "Eliminar cliente historico", description = "Elimina un cliente del historico por su NIF/CIF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente del historico"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @DeleteMapping("/{nifCif}")
    public ResponseEntity<String> eliminarCliente(@Parameter(description = "NIF/CIF del cliente a eliminar", required = true) @PathVariable String nifCif) {
        try {
            clienteHistoricoService.eliminarCliente(nifCif);
            return ResponseEntity.ok("Cliente eliminado correctamente");
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
    public List<ClienteAppHistorico> buscarPorFiltros(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClienteAppHistorico.class))
            )
            @RequestBody Map<String, FiltroCliente> filtros) {
        return clienteHistoricoService.buscarPorFiltros(filtros);
    }

    @Operation(summary = "Buscar clientes en el historico por nombre", description = "Busca clientes por coincidencia parcial de nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(schema = @Schema(implementation = ClienteAppHistorico.class)))
    })
    @GetMapping("/buscar/nombre")
    public List<ClienteAppHistorico> buscarPorNombre(@Parameter(description = "Nombre a buscar", required = true)@RequestParam String nombre) {
        return clienteHistoricoService.buscarPorNombre(nombre);
    }

    /*Puede que crear metodos para aniadir usuarios y usuario*/

}
