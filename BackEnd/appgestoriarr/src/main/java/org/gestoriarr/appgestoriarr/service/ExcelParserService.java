package org.gestoriarr.appgestoriarr.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.model.enums.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelParserService {

    public List<ClienteApp> parsear(MultipartFile file) throws IOException {
        List<ClienteApp> clientes = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Columnas del Excel:
                // 0  Nombre Comercial  → ignorar
                // 1  Nombre            → nombre
                // 2  NIF/CIF           → nifCif
                // 3  Teléfono          → telefono (lógica combinada)
                // 4  Móvil             → telefono (lógica combinada)
                // 5  Correo            → ignorar
                // 6  Tipo Cliente      → tipoCliente
                // 7  Fecha Nacimiento  → fechaNacimiento
                // 8  Código Postal     → ignorar
                // 9  Dirección         → ignorar
                // 10 Fecha Alta        → ignorar
                // 11 Fecha Baja        → ignorar
                // 12 IBAN              → últimos 5 dígitos → numerosCC
                // 13 País              → ignorar
                // 14 Población y Prov  → ignorar

                String telefono = resolverTelefono(
                        getString(row, 3),
                        getString(row, 4)
                );

                String iban = getString(row, 12);
                String numerosCC = extraerUltimos5(iban);

                ClienteApp cliente = ClienteApp.builder()
                        .nombre(getString(row, 1))
                        .nifCif(getString(row, 2))
                        .telefono(telefono)
                        .tipoCliente(getEnum(row, 6, TipoCliente.class))
                        .fechaNacimiento(getDate(row, 7))
                        .numerosCC(numerosCC)
                        // Resto de campos con valores por defecto
                        .datosFiscalesDescargados(false)
                        .importe("0")
                        .tipoFacturado(TipoFacturado.FACTURADONO)
                        .recogidaDatos(TipoRecogidaDatos.FACTURARELLENANO)
                        .excelDatosElaboracion(false)
                        .borrador(TipoBorrador.BORRADORCREADONO)
                        .presentada(TipoPresentada.CONFIRMADOPRESENTARNO)
                        .cobrado("NO")
                        .estadoCliente(EstadoCliente.CONTACTADONO)
                        .build();

                clientes.add(cliente);
            }
        }
        return clientes;
    }

    // Si telefono y movil son iguales → coge uno solo
    // Si solo hay uno → coge ese
    // Si hay los dos distintos → los une con " / "
    private String resolverTelefono(String telefono, String movil) {
        boolean hayTelefono = telefono != null && !telefono.isBlank();
        boolean hayMovil    = movil    != null && !movil.isBlank();

        if (!hayTelefono && !hayMovil) return null;
        if (!hayTelefono) return movil;
        if (!hayMovil)    return telefono;
        if (telefono.equals(movil)) return telefono;

        return telefono + " / " + movil;
    }

    // Extrae los últimos 5 caracteres del IBAN ignorando espacios
    private String extraerUltimos5(String iban) {
        if (iban == null || iban.isBlank()) return null;
        String limpio = iban.replaceAll("\\s+", "");
        if (limpio.length() < 5) return limpio;
        return limpio.substring(limpio.length() - 5);
    }



    private String getString(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> null;
        };
    }

    private Date getDate(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : null;
    }

    private <T extends Enum<T>> T getEnum(Row row, int col, Class<T> enumClass) {
        String valor = getString(row, col);
        if (valor == null) return null;
        try {
            return Enum.valueOf(enumClass, valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}