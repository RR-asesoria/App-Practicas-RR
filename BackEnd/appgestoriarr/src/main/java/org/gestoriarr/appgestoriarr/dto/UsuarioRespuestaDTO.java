package org.gestoriarr.appgestoriarr.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.gestoriarr.appgestoriarr.model.enums.Rol;

@Builder
@Getter
@AllArgsConstructor
public class UsuarioRespuestaDTO {

	String uid;

	private String correo;
	
	private String nombre;
	
	private Rol role;
	
}
