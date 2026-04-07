package org.gestoriarr.appgestoriarr.service;

import java.util.List;
import java.util.Optional;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.gestoriarr.appgestoriarr.dto.CambioPasswordDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.mapper.UsuarioMapper;
import org.gestoriarr.appgestoriarr.model.Usuario;

import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;


@Service
public class AdminService {

	private final UsuarioRepo repository;
	
	public AdminService(UsuarioRepo repo){
		this.repository=repo;
	}

	//CREATE
	public void crearUsuario(UsuarioCreacionDTO dto) throws FirebaseAuthException {

		Usuario usuario = new Usuario();

		try {

			if (repository.findByName(dto.getNombre()).isPresent()){
				throw new IllegalStateException("El nombre de usuario debe ser único");
			}

			if (repository.findByEmail(dto.getCorreo()).isPresent()){
				throw new IllegalStateException("El email ingresado ya existe");
			}

			UserRecord userRecord = FirebaseAuth.getInstance()
					.createUser(new UserRecord.CreateRequest()
							.setEmail(dto.getCorreo())
							.setPassword(dto.getPsw())
					);

			usuario = UsuarioMapper
					.toEntity(userRecord.getUid(),dto);

			repository.save(usuario);

		}catch (Exception e){
			FirebaseAuth.getInstance().deleteUser(usuario.getUid());
		}

	}

	//READ
	public Usuario encontrarPorId(String uid) throws Exception   {
		return repository.findById(uid).orElseThrow();
	}

	public Optional<Usuario> encontrarPorEmail(String email) throws Exception{
		return repository.findByEmail(email);
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

		UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid);

		if (dto.getCorreo()!=null){
			request.setEmail(dto.getCorreo());
		}

		FirebaseAuth.getInstance().updateUser(request);

		repository.save(UsuarioMapper.updateFromDTO(usuario, dto));

		return true;

	}

	public void cambiarPassword(String uid, CambioPasswordDTO dto) throws Exception {

		UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
				.setPassword(dto.getPasswordNueva());

		FirebaseAuth.getInstance().updateUser(request);

	}

	//DELETE
    public void eliminarUsuario(String uid) throws FirebaseAuthException {

		FirebaseAuth.getInstance().deleteUser(uid);

		repository.deleteById(uid);
    }
}
