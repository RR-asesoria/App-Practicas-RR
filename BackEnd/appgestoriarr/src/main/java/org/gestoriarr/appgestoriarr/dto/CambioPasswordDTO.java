package org.gestoriarr.appgestoriarr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CambioPasswordDTO {
	
	@NotBlank(message = "Contraseña obligatoria.")
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
	        message = "La contraseña debe tener mayúsculas, minúsculas, número y símbolo"	
			)
	private String password;
	
	
}
