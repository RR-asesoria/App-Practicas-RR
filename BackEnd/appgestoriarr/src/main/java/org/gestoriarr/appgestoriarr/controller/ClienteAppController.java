package org.gestoriarr.appgestoriarr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.service.ClienteAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // CREAR CLIENTE
    @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente en la base de datos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado correctamente"),
            @ApiResponse(responseCode = "409", description = "El cliente ya existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> crearCliente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del cliente a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClienteApp.class))
            )
            @RequestBody ClienteApp cliente) {
        try {
            clienteService.crearCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cliente creado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // OBTENER CLIENTE POR DNI
    @Operation(summary = "Obtener cliente", description = "Obtiene un cliente por su NIF/CIF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = ClienteApp.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @GetMapping("/{nifCif}")
    public ResponseEntity<ClienteApp> obtenerCliente(
            @Parameter(description = "NIF/CIF del cliente", required = true) @PathVariable String nifCif) {
        try {
            ClienteApp cliente = clienteService.obtenerCliente(nifCif);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // OBTENER TODOS LOS CLIENTES
    @Operation(summary = "Obtener todos los clientes", description = "Devuelve la lista completa de clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes", content = @Content(schema = @Schema(implementation = ClienteApp.class)))
    })
    @GetMapping
    public List<ClienteApp> obtenerTodos() {
        return clienteService.obtenerTodos();
    }

    // ACTUALIZAR CLIENTE
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @PutMapping("/{nifCif}")
    public ResponseEntity<String> actualizarCliente(
            @Parameter(description = "NIF/CIF del cliente a actualizar", required = true) @PathVariable String nifCif,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del cliente a actualizar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClienteApp.class))
            )
            @RequestBody ClienteApp cliente) {
        try {
            cliente.setNifCif(nifCif); // Aseguramos consistencia
            clienteService.actualizarCliente(cliente);
            return ResponseEntity.ok("Cliente actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ELIMINAR CLIENTE
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su NIF/CIF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @DeleteMapping("/{nifCif}")
    public ResponseEntity<String> eliminarCliente(
            @Parameter(description = "NIF/CIF del cliente a eliminar", required = true) @PathVariable String nifCif) {
        try {
            clienteService.eliminarCliente(nifCif);
            return ResponseEntity.ok("Cliente eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //busqueda dinamica
    @Operation(summary = "Buscar clientes", description = "Busca clientes según filtros exactos seleccionados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClienteApp.class))))
    })
    @PostMapping("/buscarporfiltros")
    public List<ClienteApp> buscarPorFiltros(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClienteApp.class))
            )
            @RequestBody Map<String, Object> filtros) {
        return clienteService.buscarPorFiltros(filtros);
    }

    // BUSQUEDA POR NOMBRE PARCIAL
    @Operation(summary = "Buscar clientes por nombre", description = "Busca clientes por coincidencia parcial de nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(schema = @Schema(implementation = ClienteApp.class)))
    })
    @GetMapping("/buscar/nombre")
    public List<ClienteApp> buscarPorNombre(
            @Parameter(description = "Texto del nombre a buscar", required = true) @RequestParam String nombre) {
        return clienteService.buscarPorNombre(nombre);
    }


    //cerrar el ejercicio y pasar la casilla505 junto el año fiscal
    @PostMapping("/cierre-ejercicio")
    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cierre de ejercicio", description = "Mueve todos los clientes a histórico y actualiza casilla505")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cierre realizado correctamente"),
            //@ApiResponse(responseCode = "403", description = "No autorizado, contacte con el administrador")
    })
    public ResponseEntity<String> cierreEjercicio() {
        clienteService.cierreEjercicio();
        return ResponseEntity.ok("Cierre de ejercicio realizado correctamente");
    }


}