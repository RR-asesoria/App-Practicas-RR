package org.gestoriarr.appgestoriarr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.gestoriarr.appgestoriarr.dto.ExcelImportResponseDTO;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.service.ClienteAppService;
import org.gestoriarr.appgestoriarr.service.ExcelParserService;
import org.gestoriarr.appgestoriarr.service.ExcelValidatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clientes/excel")
public class ExcelController {

    private final ExcelParserService parserService;
    private final ClienteAppService clienteService;
    private final ExcelValidatorService validatorService;

    public ExcelController(ExcelParserService parserService,
                           ClienteAppService clienteService,
                           ExcelValidatorService validatorService) {
        this.parserService = parserService;
        this.clienteService = clienteService;
        this.validatorService = validatorService;
    }

    @Operation(summary = "Importar clientes desde Excel", description = "Lee un archivo .xlsx y crea los clientes en la base de datos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes importados correctamente"),
            @ApiResponse(responseCode = "400", description = "Formato de Excel incorrecto", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error al procesar el archivo", content = @Content)
    })
    @PostMapping(value = "/importar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExcelImportResponseDTO> importar(
            @RequestParam("file") MultipartFile file) {

        try {

            ExcelValidatorService.ResultadoValidacion validacion = validatorService.validar(file);
            if (!validacion.valido()) {
                return ResponseEntity.badRequest().body(
                        ExcelImportResponseDTO.builder()
                                .creados(0)
                                .actualizados(0)
                                .filasSinNif(List.of())
                                .yaExistian(List.of())
                                .error(validacion.error())
                                .build()
                );
            }

            ExcelParserService.ResultadoParseo resultado = parserService.parsear(file);

            List<String> yaExistian = new ArrayList<>();
            int creados = 0;

            for (ClienteApp cliente : resultado.clientes()) {
                try {
                    clienteService.crearCliente(cliente);
                    creados++;
                    System.out.println(cliente);
                } catch (RuntimeException e) {
                    try {
                        clienteService.actualizarDatosBasicos(cliente);
                        yaExistian.add(cliente.getNifCif());
                    } catch (RuntimeException ex) {
                        yaExistian.add("ERROR-" + cliente.getNifCif());
                    }
                }
            }

            return ResponseEntity.ok(ExcelImportResponseDTO.builder()
                    .creados(creados)
                    .actualizados(yaExistian.size())
                    .filasSinNif(resultado.filasSinNif())
                    .yaExistian(yaExistian)
                    .error(null)
                    .build());

        } catch (Throwable t) {
            t.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ExcelImportResponseDTO.builder()
                            .creados(0)
                            .actualizados(0)
                            .filasSinNif(List.of())
                            .yaExistian(List.of())
                            .error(t.getClass().getSimpleName() + ": " + t.getMessage())
                            .build());
        }
    }
}