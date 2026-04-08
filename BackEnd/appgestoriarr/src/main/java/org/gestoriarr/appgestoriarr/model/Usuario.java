package org.gestoriarr.appgestoriarr.model;


import com.google.firebase.database.Exclude;
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

    @Exclude
    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return uid;
    }
}
