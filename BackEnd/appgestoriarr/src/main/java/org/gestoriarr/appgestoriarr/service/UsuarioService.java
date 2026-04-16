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

			if (repository.findByName(dto.getNombre()).isPresent()){
				throw new IllegalStateException("El nombre de usuario debe ser único");
			}

			if (repository.findByEmail(dto.getCorreo()).isPresent()){
				throw new IllegalStateException("El email ingresado ya existe");
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

			throw new RuntimeException("No pudo crearse el usuario", e);
		}

	}

	//READ
	public UsuarioRespuestaDTO encontrarPorId(String uid) throws Exception   {

		Optional<Usuario> usuario = repository.findById(uid);

		if (usuario.isEmpty()){
			throw new RuntimeException("El usuario no fue encontrado");
		}

		return UsuarioMapper.toDTO(usuario.get());
	}

	public UsuarioRespuestaDTO encontrarPorEmail(String email) throws Exception{

		Optional<Usuario> usuario = repository.findByEmail(email);

		if (usuario.isEmpty()){
			throw new RuntimeException("El usuario no fue encontrado");
		}

		return UsuarioMapper.toDTO(usuario.get());
	}

	public UsuarioRespuestaDTO encontrarPorNombre(String nombre) throws Exception {
		Optional<Usuario> usuario = repository.findByName(nombre);

		if (usuario.isEmpty()){
			throw new RuntimeException("El usuario no fue encontrado");
		}

		return UsuarioMapper.toDTO(usuario.get());
	}

    public List<UsuarioRespuestaDTO> obtenerTodos() throws Exception{

		List<Usuario> usuarios = repository.findAll();

		return usuarios.stream()
				.map(UsuarioMapper::toDTO)
				.toList();
	}

	//UPDATE

	public String AdminActualizarUsuario(String correo, UsuarioActualizarDTO dto) throws Exception {

		Optional<Usuario> usuarioOriginal = Optional.empty();

		Usuario usuarioActualizacion;

		UserRecord.UpdateRequest request = null;

		try {

			usuarioOriginal = repository.findByEmail(correo);

			if (usuarioOriginal.isEmpty()){
				throw new RuntimeException("El usuario no fue encontrado");
			}

			usuarioActualizacion =
			UsuarioMapper.updateFromDTO(usuarioOriginal.get(), dto);

			request = new UserRecord.UpdateRequest(usuarioOriginal.get().getUid());

			if (dto.getCorreo()!=null){
				request.setEmail(dto.getCorreo());
			}

			FirebaseAuth.getInstance().updateUser(request);
			repository.save(usuarioActualizacion);

			return "Usuario actualizado.";

		} catch (Exception e) {

			if (usuarioOriginal.isPresent()){
				repository.save(usuarioOriginal.get());
				if (request != null) {
					request.setEmail(usuarioOriginal.get().getCorreo());
				} else {
					throw new AssertionError();
				}
			}

			throw new RuntimeException("El usuario no pudo ser actualizado. ", e);
		}

	}

	public void cambiarPasswordAdmin(String correo, CambioPasswordDTO dto) throws Exception {

		Optional<Usuario> usuario = repository.findByEmail(correo);

		if (usuario.isEmpty()){
			throw new RuntimeException("El usuario no fue encontrado");
		}

		UserRecord.UpdateRequest request = new UserRecord
				.UpdateRequest(usuario.get().getUid())
				.setPassword(dto.getPasswordNueva());

		FirebaseAuth.getInstance().updateUser(request);

	}

	public String actualizar(String uid, UsuarioActualizarDTO dto) throws Exception {

		Optional<Usuario> usuarioOriginal = Optional.empty();

		Usuario usuarioActualizacion;

		UserRecord.UpdateRequest request = null;

		try {

			usuarioOriginal = repository.findById(uid);

			if (usuarioOriginal.isEmpty()){
				throw new RuntimeException("El usuario no fue encontrado");
			}

			usuarioActualizacion = usuarioOriginal.get();
			UsuarioMapper.updateFromDTO(usuarioActualizacion, dto);

			request = new UserRecord.UpdateRequest(uid);

			if (dto.getCorreo()!=null){
				request.setEmail(dto.getCorreo());
			}

			FirebaseAuth.getInstance().updateUser(request);
			repository.save(usuarioActualizacion);

			return "Usuario actualizado.";

		} catch (Exception e) {

			if (usuarioOriginal.isPresent()){
				repository.save(usuarioOriginal.get());
                if (request != null) {
                    request.setEmail(usuarioOriginal.get().getCorreo());
                } else {
                    throw new AssertionError();
                }
            }

			throw new RuntimeException("El usuario no pudo ser actualizado. ", e);
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
}
