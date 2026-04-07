package org.gestoriarr.appgestoriarr.repository;

import org.gestoriarr.appgestoriarr.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepo extends FirebaseRepositoryImpl<Usuario> {

    protected UsuarioRepo() {
        super(Usuario.class, "usuarios");
    }

    public Optional<Usuario> findByEmail(String email) throws Exception{
        List<Usuario> usuarios = findByField("email", email);
        return usuarios.isEmpty() ? null : Optional.ofNullable(usuarios.getFirst());
    }

    public Optional<Usuario> findByName(String name) throws Exception {

        List<Usuario> list = findByField("nombre",name);
        return list.isEmpty() ? Optional.empty() : Optional.of( list.getFirst());
    }



}
