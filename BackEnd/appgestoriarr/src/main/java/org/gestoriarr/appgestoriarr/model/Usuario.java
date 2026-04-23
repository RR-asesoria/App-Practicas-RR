package org.gestoriarr.appgestoriarr.model;

import lombok.*;
import org.gestoriarr.appgestoriarr.model.enums.Rol;
import org.gestoriarr.appgestoriarr.repository.Identificable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class Usuario implements Identificable {

    private String uid;

    private String correo;

    private String nombre;

    private Rol role;

    @Override
    public String getId() {
        return uid;
    }

    public void setId(String id) {
       uid=id;
    }
}
