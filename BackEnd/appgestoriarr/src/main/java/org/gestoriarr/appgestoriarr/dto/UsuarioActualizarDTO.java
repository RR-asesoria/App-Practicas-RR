package org.gestoriarr.appgestoriarr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gestoriarr.appgestoriarr.model.enums.Rol;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioActualizarDTO {
	
	@Email(message = "Formato de email incorrecto.")
	private String correo;

	@Size(min = 2, max = 50)
	private String nombre;

}
