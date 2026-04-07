package org.gestoriarr.appgestoriarr.dto;

import jakarta.validation.constraints.NotBlank;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gestoriarr.appgestoriarr.validation.ValidPassword;

@Getter
@AllArgsConstructor
public class CambioPasswordDTO {

	@NotBlank
	private String passwordActual;

	@NotBlank
	@ValidPassword
	private String passwordNueva;
	
	
}
