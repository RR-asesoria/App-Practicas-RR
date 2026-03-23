package org.gestoriarr.appgestoriarr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.service.ClienteAppService;
import org.gestoriarr.appgestoriarr.service.ExcelParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/excel")
public class ExcelController {

    private final ExcelParserService parserService;
    private final ClienteAppService clienteService;

    public ExcelController(ExcelParserService parserService, ClienteAppService clienteService) {
        this.parserService = parserService;
        this.clienteService = clienteService;
    }

    @Operation(summary = "Importar clientes desde Excel", description = "Lee un archivo .xlsx y crea los clientes en la base de datos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes importados correctamente"),
            @ApiResponse(responseCode = "500", description = "Error al procesar el archivo", content = @Content)
    })
    @PostMapping(value = "/importar", consumes = "multipart/form-data")
    public ResponseEntity<String> importar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Archivo Excel .xlsx con los clientes a importar",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("file") MultipartFile file) {
        try {
            List<ClienteApp> clientes = parserService.parsear(file);
            clientes.forEach(clienteService::crearCliente);
            return ResponseEntity.ok("Importados " + clientes.size() + " clientes correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al importar: " + e.getMessage());
        }
    }
}