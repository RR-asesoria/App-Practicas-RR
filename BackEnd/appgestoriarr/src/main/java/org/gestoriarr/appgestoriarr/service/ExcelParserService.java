package org.gestoriarr.appgestoriarr.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gestoriarr.appgestoriarr.model.ClienteApp;
import org.gestoriarr.appgestoriarr.model.enums.*;
import org.gestoriarr.appgestoriarr.repository.ClienteAppRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelParserService {

    public record ResultadoParseo(List<ClienteApp> clientes, List<Integer> filasSinNif) {}

    private final ClienteAppRepo repo;

    public ExcelParserService(ClienteAppRepo repo) {
        this.repo = repo;
    }

    // ── Método principal ──────────────────────────────────────────────────────

    public ResultadoParseo parsear(MultipartFile file) throws IOException {
        List<ClienteApp> clientes = new ArrayList<>();
        List<Integer> filasSinNif = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String nifCif = getString(row, 2);

                if (nifCif == null || nifCif.isBlank()) {
                    filasSinNif.add(i + 1);
                    nifCif = generarNifPorDefecto();
                }

                ClienteApp cliente = ClienteApp.builder()
                        .nombre(getString(row, 1))
                        .nifCif(nifCif)
                        .telefono(resolverTelefono(getString(row, 3), getString(row, 4)))
                        .correoElectronico(getString(row, 5))
                        .tipoCliente(parseTipoCliente(row, 6))
                        .fechaNacimiento(getDate(row, 7))
                        .numerosCC(extraerUltimos5(getString(row, 12)))
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

        // Deduplicar: si hay NIFs repetidos, quedarse con el último
        Map<String, ClienteApp> mapaDeduplicado = new LinkedHashMap<>();
        for (ClienteApp cliente : clientes) {
            mapaDeduplicado.put(cliente.getNifCif(), cliente);
        }
        List<ClienteApp> clientesDeduplicados = new ArrayList<>(mapaDeduplicado.values());

        return new ResultadoParseo(clientesDeduplicados, filasSinNif);
    }

    // ── Lógica de negocio ─────────────────────────────────────────────────────

    private int contadorSinNif = -1; // -1 = no inicializado

    private String generarNifPorDefecto() {
        if (contadorSinNif == -1) {
            // Primera vez: consulta Firebase para saber cuántos SIN-NIF hay ya
            contadorSinNif = (int) repo.findAll().stream()
                    .filter(c -> c.getNifCif() != null && c.getNifCif().startsWith("SIN-NIF-"))
                    .count();
        }
        contadorSinNif++;
        String nif = "SIN-NIF-" + String.format("%03d", contadorSinNif);
        return nif;
    }

    private String resolverTelefono(String telefono, String movil) {
        boolean hayTelefono = telefono != null && !telefono.isBlank();
        boolean hayMovil    = movil    != null && !movil.isBlank();

        if (!hayTelefono && !hayMovil) return null;
        if (!hayTelefono) return movil;
        if (!hayMovil)    return telefono;
        if (telefono.equals(movil)) return telefono;

        return telefono + " / " + movil;
    }

    private String extraerUltimos5(String iban) {
        if (iban == null || iban.isBlank()) return null;
        String limpio = iban.replaceAll("\\s+", "");
        if (limpio.length() < 5) return limpio;
        return limpio.substring(limpio.length() - 5);
    }

    private TipoCliente parseTipoCliente(Row row, int col) {
        String valor = getString(row, col);
        if (valor == null) return null;

        return switch (valor.trim().toUpperCase()) {
            case "RENTA"              -> TipoCliente.AUTONOMO;
            case "RENTA PARTICULARES" -> TipoCliente.PARTICULARES;
            case "NORESIDENTE"        -> TipoCliente.NORESIDENTE;
            default                   -> null;
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            // Forzar UTC para evitar el desfase de zona horaria
            LocalDate localDate = cell.getLocalDateTimeCellValue().toLocalDate();
            return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
        }

        if (cell.getCellType() == CellType.STRING) {
            String valor = cell.getStringCellValue().trim();
            try {
                LocalDate localDate = LocalDate.parse(valor, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (Exception e) {
                return null;
            }
        }

        return null;
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