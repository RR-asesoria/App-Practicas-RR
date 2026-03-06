package org.gestoriarr.appgestoriarr.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//CLASE PARA ELIMINAR, YA NO ES NECESARIA PERO HAY QUE QUITAR LA LOGICA DE HISTÓRICO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroCliente {


    private Object valorIgual;


    private String valorParcial;

    private Object valorMin;


    private Object valorMax;


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