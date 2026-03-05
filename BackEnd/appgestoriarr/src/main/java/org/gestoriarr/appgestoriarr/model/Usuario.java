package org.gestoriarr.appgestoriarr.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario {
    private String nombre;
    private String clave;
    private Rol rol;
}
