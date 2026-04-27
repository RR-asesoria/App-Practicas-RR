package org.gestoriarr.appgestoriarr.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExcelValidatorService {

    private static final Map<Integer, String> COLUMNAS_ESPERADAS = Map.ofEntries(
            Map.entry(0,  "NOMBRE COMERCIAL"),
            Map.entry(1,  "NOMBRE"),
            Map.entry(2,  "NIF/CIF"),
            Map.entry(3,  "TELÉFONO"),
            Map.entry(4,  "MÓVIL"),
            Map.entry(5,  "CORREO ELECTRÓNICO"),
            Map.entry(6,  "TIPO CLIENTE"),
            Map.entry(7,  "FECHA NACIMIENTO"),
            Map.entry(8,  "CÓDIGO POSTAL"),
            Map.entry(9,  "DIRECCIÓN"),
            Map.entry(10, "FECHA ALTA"),
            Map.entry(11, "FECHA BAJA"),
            Map.entry(12, "IBAN"),
            Map.entry(13, "PAÍS"),
            Map.entry(14, "POBLACIÓN"),
            Map.entry(15, "PROVINCIA")
    );

    public record ResultadoValidacion(boolean valido, String error) {}

    public ResultadoValidacion validar(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls"))) {
            return new ResultadoValidacion(false, "El archivo debe ser .xlsx o .xls");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row cabecera = sheet.getRow(0);

            if (cabecera == null) {
                return new ResultadoValidacion(false, "El archivo no es el Excel de clientes correcto");
            }

            for (Map.Entry<Integer, String> entry : COLUMNAS_ESPERADAS.entrySet()) {
                Cell cell = cabecera.getCell(entry.getKey(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                String valorActual = cell != null ? cell.getStringCellValue().trim().toUpperCase() : "";
                if (!valorActual.equals(entry.getValue())) {
                    return new ResultadoValidacion(false, "El archivo importado no corresponde con el excel correcto. Recuerda que si se han realizado cambios en las cabeceras de documento, este no podrá ser importado.");
                }
            }
        }

        return new ResultadoValidacion(true, null);
    }
}