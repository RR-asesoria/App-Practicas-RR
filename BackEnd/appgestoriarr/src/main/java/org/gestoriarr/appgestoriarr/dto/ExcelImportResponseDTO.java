package org.gestoriarr.appgestoriarr.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ExcelImportResponseDTO {
    private int creados;
    private int actualizados;
    private List<Integer> filasSinNif;
    private List<String> yaExistian;
    private String error;
}