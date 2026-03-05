package org.gestoriarr.appgestoriarr.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FiltroCliente permite filtrar cualquier campo de ClienteApp.
 * Puede filtrar por:
 *  - valorIgual (igualdad exacta)
 *  - valorMin / valorMax (rangos numéricos o fechas)
 *  - valorParcial (texto que empieza con ...)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroCliente {

    /** Igualdad exacta */
    private Object valorIgual;

    /** Búsqueda parcial (String) */
    private String valorParcial;

    /** Valor mínimo (Number o fecha) */
    private Object valorMin;

    /** Valor máximo (Number o fecha) */
    private Object valorMax;

    /**
     * Métodos de fábrica para simplificar creación de filtros:
     */
    public static FiltroCliente igual(Object valor) {
        return FiltroCliente.builder().valorIgual(valor).build();
    }

    public static FiltroCliente parcial(String valor) {
        return FiltroCliente.builder().valorParcial(valor).build();
    }

    public static FiltroCliente rango(Object min, Object max) {
        return FiltroCliente.builder().valorMin(min).valorMax(max).build();
    }

    public static FiltroCliente minimo(Object min) {
        return FiltroCliente.builder().valorMin(min).build();
    }

    public static FiltroCliente maximo(Object max) {
        return FiltroCliente.builder().valorMax(max).build();
    }
}