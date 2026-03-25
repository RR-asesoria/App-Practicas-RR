package org.gestoriarr.appgestoriarr.repository;

import org.gestoriarr.appgestoriarr.model.Usuario;

import java.util.List;

public class UsuarioRepo extends FirebaseRepositoryImpl<Usuario> {

    protected UsuarioRepo() {
        super(Usuario.class, "usuarios");
    }

    public Usuario findByEmail(String email) throws Exception{
        List<Usuario> usuarios = findByField("email", email);
        return usuarios.isEmpty() ? null : usuarios.getFirst();
    }



}
