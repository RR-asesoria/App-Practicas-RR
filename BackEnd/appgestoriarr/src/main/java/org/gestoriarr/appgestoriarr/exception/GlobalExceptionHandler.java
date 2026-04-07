package org.gestoriarr.appgestoriarr.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex){
		
		List<String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
		
		return ResponseEntity.badRequest().body(
				new ErrorResponse(
					HttpStatus.BAD_REQUEST.value(),
					"User validation error",
					errors.toString(),
					LocalDateTime.now()
					)
				);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegarArgument(IllegalArgumentException ex){
		
		return ResponseEntity.badRequest().body(
				new ErrorResponse(
						400, 
						"Bad Request", 
						ex.getMessage(), 
						LocalDateTime.now()
						)
		);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneral(Exception ex) {
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ErrorResponse(
						500, 
						"Error interno", 
						ex.getMessage(), 
						LocalDateTime.now())
				);
		
	}
	
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ErrorResponse(404, 
						"Not Found", 
						ex.getMessage(), 
						LocalDateTime.now())
				);
	}
	
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(
					new ErrorResponse(
					401, 
					"Unauthorized",
					ex.getMessage(),
					LocalDateTime.now()
					)
						);
	}
	
	public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				new ErrorResponse(
						409, 
						"Conflict", 
						ex.getMessage(), 
						LocalDateTime.now())
				);
				
		
	}
	
}
