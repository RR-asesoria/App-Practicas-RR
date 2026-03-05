package org.gestoriarr.appgestoriarr.model;

import lombok.*;
import org.gestoriarr.appgestoriarr.model.enums.Rol;

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
