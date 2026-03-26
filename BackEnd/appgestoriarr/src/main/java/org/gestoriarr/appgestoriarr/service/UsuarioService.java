package org.gestoriarr.appgestoriarr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.UserRecord;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.mapper.UsuarioMapper;
import org.gestoriarr.appgestoriarr.model.Usuario;

import org.gestoriarr.appgestoriarr.repository.UsuarioRepo;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


@Service
public class UsuarioService {

	private final UsuarioRepo repository;
	
	public UsuarioService(UsuarioRepo repo){
		this.repository=repo;
	}
	

    public void guardarUsuario(UsuarioCreacionDTO dto) throws Exception{

		UserRecord userRecord = FirebaseAuth.getInstance()
				.createUser(new UserRecord.CreateRequest()
						.setEmail(dto.getCorreo())
						.setPassword(dto.getPsw())
				);

		Usuario usuario = UsuarioMapper
				.toEntity(userRecord.getUid(),dto);




		repository.save(usuario);
    }
   
    
    public Usuario obtenerUsuario(String uid) throws Exception   {
    	
    	return repository.findById(uid).get();
    }

    public List<Usuario> obtenerTodos() throws Exception{
		
    	return repository.findAll();
    	
	}
    
    public void eliminarUsuario(String uid) {
    	repository.deleteById(uid);
    }
    
    
    public void asignarRol(String uid, String rol)throws FirebaseAuthException {
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", rol);
		
		FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
		
	}
    
    
    
    
}
