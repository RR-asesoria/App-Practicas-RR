package org.gestoriarr.appgestoriarr.model;

import lombok.*;
import org.gestoriarr.appgestoriarr.model.enums.Rol;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class Usuario {
    private String uid;

    private String correo;

    private String nombre;

    private Rol role;

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return uid;
    }
}
