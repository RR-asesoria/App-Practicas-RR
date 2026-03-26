package org.gestoriarr.appgestoriarr.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gestoriarr.appgestoriarr.model.enums.Rol;

@Getter
@AllArgsConstructor
public class UsuarioCreacionDTO {
	
	@NotBlank
	@Email(message = "Formato de email incorrecto.")
	private String correo;
	
	@NotBlank
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
	        message = "La contraseña debe tener mayúsculas, minúsculas, número y símbolo"	
			)
	private String psw;
	
	@NotBlank
	@Size(min = 2, max = 50)
	private String nombre;
}
