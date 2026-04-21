package org.gestoriarr.appgestoriarr.service;

import java.util.List;
import java.util.Optional;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.AllArgsConstructor;
import org.gestoriarr.appgestoriarr.dto.CambioPasswordDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioRespuestaDTO;
import org.gestoriarr.appgestoriarr.exception.UserNotFoundException;
import org.gestoriarr.appgestoriarr.mapper.UsuarioMapper;
import org.gestoriarr.appgestoriarr.model.Usuario;

import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;


@Service
@AllArgsConstructor
public class UsuarioService {

	private final UsuarioRepo repository;

	//CREATE
	public void crearUsuario(UsuarioCreacionDTO dto) throws FirebaseAuthException {

		UserRecord userRecord = null;
		Usuario usuario;

		try {

			if (repository.findByEmail(dto.getCorreo()).isPresent()){
				throw new IllegalStateException("El email ingresado ya existe.");
			}

			if (repository.findByName(dto.getNombre()).isPresent()){
				throw new IllegalStateException("El nombre de usuario debe ser único.");
			}

			userRecord = FirebaseAuth.getInstance()
					.createUser(new UserRecord.CreateRequest()
							.setEmail(dto.getCorreo())
							.setPassword(dto.getPsw())
					);

			usuario = UsuarioMapper
					.toEntity(userRecord.getUid(),dto);

			repository.save(usuario);

		} catch (Exception e) {
			if (userRecord != null) {
				FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
			}

			throw new IllegalStateException("User could not be created. "+e.getMessage());
		}

	}

	//READ
	public Usuario encontrarPorIdInterno(String uid) throws Exception {
		return repository.findById(uid)
				.orElseThrow(()-> new UserNotFoundException("User not found"));
	}

	public Usuario encontrarPorEmailInterno(String email) throws Exception {
		return repository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	public Usuario encontrarPorNombreInterno(String nombre) throws Exception {
		return repository.findByName(nombre)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	public UsuarioRespuestaDTO encontrarPorId(String uid){
		try {
			Usuario usuario = encontrarPorIdInterno(uid);
			return UsuarioMapper.toDTO(usuario);
		} catch (Exception e) {
			throw new RuntimeException("Database error."+e.getMessage());
		}
	}

	public UsuarioRespuestaDTO encontrarPorEmail(String email){
		try {
			Usuario usuario = encontrarPorEmailInterno(email);
			return UsuarioMapper.toDTO(usuario);
		} catch (Exception e) {
			throw new RuntimeException("Database error."+e.getMessage());
		}
    }

	public UsuarioRespuestaDTO encontrarPorNombre(String nombre) {
		try {
			Usuario usuario = encontrarPorNombreInterno(nombre);
			return UsuarioMapper.toDTO(usuario);
		} catch (Exception e) {
			throw new RuntimeException("Database error."+e.getMessage());
		}
    }

    public List<UsuarioRespuestaDTO> obtenerTodos(){
		try {
			return repository.findAll().stream()
					.map(UsuarioMapper::toDTO)
					.toList();
		} catch (Exception e) {
			throw new RuntimeException("Database error."+e.getMessage());
		}
	}

	//UPDATE
	public String AdminActualizarUsuario(String correo, UsuarioActualizarDTO dto) throws Exception {

		Usuario original= new Usuario();
		Usuario update;
		UserRecord.UpdateRequest request = null;

		try {
			original = encontrarPorEmailInterno(correo);
			update= UsuarioMapper.updateFromDTO(original, dto);
			request = new UserRecord.UpdateRequest(original.getUid());

			if (dto.getCorreo()!=null){
				request.setEmail(dto.getCorreo());
			}

			FirebaseAuth.getInstance().updateUser(request);
			repository.save(update);
			return "Usuario actualizado";

		} catch (Exception e) {
			repository.save(original);
			if (request != null) {
				request.setEmail(original.getCorreo());
			} else {
					throw new AssertionError("Rollback error.");
			}
			throw new RuntimeException("The user could not be updated.", e);
		}

	}

	public void cambiarPasswordAdmin(String correo, CambioPasswordDTO dto) throws Exception {

		Usuario usuario = encontrarPorEmailInterno(correo);

		UserRecord.UpdateRequest request = new UserRecord
				.UpdateRequest(usuario.getUid())
				.setPassword(dto.getPasswordNueva());

		FirebaseAuth.getInstance().updateUser(request);

	}

	public String actualizar(String uid, UsuarioActualizarDTO dto) throws Exception {

		Usuario original = new Usuario();
		Usuario update;
		UserRecord.UpdateRequest request = null;

		try {

			original = encontrarPorIdInterno(uid);
			update = UsuarioMapper.updateFromDTO(original, dto);
			request = new UserRecord.UpdateRequest(uid);

			if (dto.getCorreo()!=null){
				request.setEmail(dto.getCorreo());
			}

			FirebaseAuth.getInstance().updateUser(request);
			repository.save(update);
			return "Usuario actualizado.";

		} catch (Exception e) {
			repository.save(original);
			if (request != null) {
				request.setEmail(original.getCorreo());
			} else {
				throw new AssertionError("Rollback error.");
			}

			throw new RuntimeException("The user could not be updated ", e);
		}

	}


	public void cambiarPasswordUser(String uid, CambioPasswordDTO dto) throws Exception {
		UserRecord.UpdateRequest request = new UserRecord
				.UpdateRequest(uid)
				.setPassword(dto.getPasswordNueva());
		FirebaseAuth.getInstance().updateUser(request);

	}

	//DELETE
    public void eliminarUsuario(String uid) throws FirebaseAuthException {
		FirebaseAuth.getInstance().deleteUser(uid);
		repository.deleteById(uid);
    }

	public void eliminarUsuarioPorEmail(String email) throws Exception {
		Usuario usuario = encontrarPorEmailInterno(email);
		FirebaseAuth.getInstance().deleteUser(usuario.getUid());
		repository.deleteById(usuario.getId());
	}


}
