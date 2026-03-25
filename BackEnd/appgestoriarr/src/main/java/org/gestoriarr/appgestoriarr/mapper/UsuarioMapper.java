package org.gestoriarr.appgestoriarr.mapper;


import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioRespuestaDTO;
import org.gestoriarr.appgestoriarr.model.Usuario;

public class UsuarioMapper {
	
	public static UsuarioRespuestaDTO toDTO(Usuario usuario) {
		
		return new UsuarioRespuestaDTO(
				usuario.getCorreo(), 
				usuario.getNombre(), 
				usuario.getRole()
				);
	}
	
	public static Usuario toEntity(String uid, UsuarioCreacionDTO dto) {
		
		return new Usuario(uid,
				dto.getCorreo(), 
				dto.getNombre(), 
				dto.getRole() 
				);
		
	}
	
	public static Usuario updateFromDTO(Usuario usuario, UsuarioActualizarDTO dto) {
		
		if(dto.getCorreo()!=null) {
			usuario.setCorreo(dto.getCorreo());
		}
		
		if(dto.getNombre()!=null) {
			usuario.setNombre(dto.getNombre());
		}
		
		if(dto.getRole()!=null) {
			usuario.setRole(dto.getRole());
		}
		
		return usuario;
	}
	
	

}
