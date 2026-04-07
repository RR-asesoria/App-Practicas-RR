package org.gestoriarr.appgestoriarr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.firebase.auth.UserRecord;
import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.mapper.UsuarioMapper;
import org.gestoriarr.appgestoriarr.model.Usuario;

import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


@Service
public class UsuarioService {

	private final UsuarioRepo repository;
	
	public UsuarioService(UsuarioRepo repo){
		this.repository=repo;
	}

	//CREATE
	public void crearUsuario(UsuarioCreacionDTO dto) throws Exception{

		UserRecord userRecord = FirebaseAuth.getInstance()
				.createUser(new UserRecord.CreateRequest()
						.setEmail(dto.getCorreo())
						.setPassword(dto.getPsw())
				);

		Usuario usuario = UsuarioMapper
				.toEntity(userRecord.getUid(),dto);

		repository.save(usuario);

	}

	//READ
	public Usuario encontrarPorId(String uid) throws Exception   {
		return repository.findById(uid).orElseThrow();
	}

	public Optional<Usuario> encontrarPorEmail(String email) throws Exception{
		return Optional.of(repository.findByEmail(email));
	}

	public Optional<Usuario> encontrarPorNombre(String nombre) throws Exception {
		return repository.findByName(nombre);
	}

    public List<Usuario> obtenerTodos() throws Exception{
    	return repository.findAll();
	}

	//UPDATE
	public boolean actualizar(String uid, UsuarioActualizarDTO dto) throws Exception {

		Usuario usuario = encontrarPorId(uid);

		repository.save(UsuarioMapper.updateFromDTO(usuario, dto));

		return true;

	}

	//DELETE
    public void eliminarUsuario(String uid) {
    	repository.deleteById(uid);
    }
}
