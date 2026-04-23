package org.gestoriarr.appgestoriarr.service;

import java.util.List;
import java.util.Objects;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.AllArgsConstructor;
import org.gestoriarr.appgestoriarr.dto.CambioPasswordDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioRespuestaDTO;
import org.gestoriarr.appgestoriarr.exception.ExistingUserException;
import org.gestoriarr.appgestoriarr.exception.UserNotFoundException;
import org.gestoriarr.appgestoriarr.mapper.UsuarioMapper;
import org.gestoriarr.appgestoriarr.model.Usuario;

import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.springframework.security.core.context.SecurityContextHolder;
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
			if (repository.findByEmail(dto.getCorreo().toLowerCase()).isPresent()){
				throw new ExistingUserException("El email ingresado ya existe.");
			}

			if (repository.findByName(dto.getNombre().toLowerCase()).isPresent()){
				throw new ExistingUserException("El nombre ingresado ya existe");
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
			if (e instanceof ExistingUserException){
				throw new ExistingUserException(e.getMessage());
			}
			if (userRecord != null) {
				FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
			}
			throw new RuntimeException(e.getMessage());
		}

	}

	//READ
	public Usuario encontrarPorIdInterno(String uid){
		try {
            return repository.findById(uid)
                    .orElseThrow(()-> new UserNotFoundException("User not found"));
		} catch (Exception e) {
			throw new UserNotFoundException(e.getMessage());
		}

	}

	public Usuario encontrarPorEmailInterno(String email){
        try {
            return repository
                    .findByEmail(email)
                    .orElseThrow( () -> new UserNotFoundException("User not found"));
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

	public Usuario encontrarPorNombreInterno(String nombre){
		try {
			return repository.findByName(nombre)
					.orElseThrow(() -> new UserNotFoundException("User not found"));
		} catch (Exception e) {
			throw new UserNotFoundException(e.getMessage());
		}

	}

	public UsuarioRespuestaDTO encontrarPorId(String uid){
		Usuario usuario = encontrarPorIdInterno(uid);
		return UsuarioMapper.toDTO(usuario);

	}

	public UsuarioRespuestaDTO encontrarPorEmail(String email){
			Usuario usuario = encontrarPorEmailInterno(email);
			return UsuarioMapper.toDTO(usuario);
    }

	public UsuarioRespuestaDTO encontrarPorNombre(String nombre) {
			Usuario usuario = encontrarPorNombreInterno(nombre);
			return UsuarioMapper.toDTO(usuario);
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
	public void AdminActualizarUsuario(String correo, UsuarioActualizarDTO dto) throws Exception {

		Usuario original= new Usuario();
		Usuario update;
		UserRecord.UpdateRequest request = null;

		try {
			original = encontrarPorEmailInterno(correo);

			if (repository.findByName(dto.getNombre()).isPresent()){
				throw new ExistingUserException("El nombre de usuario ya existe");
			}
			if (repository.findByEmail(dto.getCorreo()).isPresent()){
				throw new ExistingUserException("El correo ya existe.");
			}

			update= UsuarioMapper.updateFromDTO(original, dto);
			request = new UserRecord.UpdateRequest(original.getUid());

			if (dto.getCorreo()!=null){
				request.setEmail(dto.getCorreo());
			}

			FirebaseAuth.getInstance().updateUser(request);
			repository.save(update);
		}
		catch (Exception e) {

			if (e instanceof UserNotFoundException) {
				throw new UserNotFoundException(e.getMessage());
			}

			if (e instanceof ExistingUserException) {
				throw new ExistingUserException(e.getMessage());
			}

			if (e instanceof NullPointerException){
				throw new IllegalArgumentException(e.getMessage());
			}

			repository.save(original);
			if (request != null) {
				request.setEmail(original.getCorreo());
			} else {
				throw new AssertionError("Rollback error.");
			}

			throw new RuntimeException("The user could not be updated." + e.getMessage());

		}

	}

	public void cambiarPasswordAdmin(String correo, CambioPasswordDTO dto) {
		try {
			Usuario usuario = encontrarPorEmailInterno(correo);

			UserRecord.UpdateRequest request = new UserRecord
					.UpdateRequest(usuario.getUid())
					.setPassword(dto.getPasswordNueva());

			FirebaseAuth.getInstance().updateUser(request);
		} catch (Exception e) {
			if (e instanceof UserNotFoundException){
				throw new UserNotFoundException(e.getMessage());
			}
			throw new RuntimeException(e);
		}
	}

	public String actualizar(String uid, UsuarioActualizarDTO dto) throws Exception {

		Usuario original = new Usuario();
		Usuario update;
		UserRecord.UpdateRequest request = null;

		try {
			original = encontrarPorIdInterno(uid);

			if (repository.findByName(dto.getNombre()).isPresent()){
				throw new ExistingUserException("El nombre de usuario ya existe");
			}
			if (repository.findByEmail(dto.getCorreo()).isPresent()){
				throw new ExistingUserException("El correo ya existe.");
			}

			update = UsuarioMapper.updateFromDTO(original, dto);
			request = new UserRecord.UpdateRequest(uid);

			if (dto.getCorreo()!=null){
				request.setEmail(dto.getCorreo());
			}

			FirebaseAuth.getInstance().updateUser(request);
			repository.save(update);
			return "Usuario actualizado.";

		} catch (Exception e) {
			if (e instanceof UserNotFoundException) {
				throw new UserNotFoundException(e.getMessage());
			}

			if (e instanceof ExistingUserException) {
				throw new ExistingUserException(e.getMessage());
			}

			if (e instanceof NullPointerException){
				throw new IllegalArgumentException(e.getMessage());
			}

			repository.save(original);
			if (request != null) {
				request.setEmail(original.getCorreo());
			} else {
				throw new AssertionError("Rollback error.");
			}

			throw new RuntimeException("The user could not be updated." + e.getMessage());
		}

	}


	public void cambiarPasswordUser(String uid, CambioPasswordDTO dto) {
		try {
			UserRecord.UpdateRequest request = new UserRecord
					.UpdateRequest(uid)
					.setPassword(dto.getPasswordNueva());
			FirebaseAuth.getInstance().updateUser(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//DELETE
    public void eliminarUsuario(String uid) {
		try {

			final String userAuthenticateUid = (String) Objects
					.requireNonNull(SecurityContextHolder.getContext()
							.getAuthentication()).getPrincipal();

			if (uid.equals(userAuthenticateUid)){
				throw new IllegalArgumentException(
						"No puedes eliminar al usuario admin.");
			}

			Usuario usuario = encontrarPorIdInterno(uid);
			FirebaseAuth.getInstance().deleteUser(usuario.getUid());
			repository.deleteById(uid);
		} catch (Exception e) {

			if (e instanceof UserNotFoundException){
				throw new UserNotFoundException(e.getMessage());
			}

			if (e instanceof IllegalArgumentException){
				throw new IllegalArgumentException(e.getMessage());
			}

			throw new RuntimeException(e);
		}

    }

	public void eliminarUsuarioPorEmail(String email) {

		try {
			Usuario usuario = encontrarPorEmailInterno(email);

			if (usuario.getRole().name().equals("ADMIN")){
				throw new IllegalArgumentException("No puedes eliminar el usuario admin.");
			}

			FirebaseAuth.getInstance().deleteUser(usuario.getUid());
			repository.deleteById(usuario.getId());
		}catch (Exception e) {

			if (e instanceof UserNotFoundException){
				throw new UserNotFoundException(e.getMessage());
			}

			if (e instanceof IllegalArgumentException){
				throw new IllegalArgumentException(e.getMessage());
			}

			throw new RuntimeException(e);
		}

	}


}
