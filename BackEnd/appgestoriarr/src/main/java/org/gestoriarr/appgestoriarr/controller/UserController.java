package org.gestoriarr.appgestoriarr.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.gestoriarr.appgestoriarr.dto.CambioPasswordDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioRespuestaDTO;
import org.gestoriarr.appgestoriarr.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService service;

    @GetMapping("/ADMIN/test")
    public String admin(Authentication auth, HttpServletRequest request) {

        String uid = auth.getName();

        return "Zona ADMIN";
    }


    //CREATE
    @PostMapping("/crearusuario")
    public ResponseEntity<String> crearUsuario(@RequestBody UsuarioCreacionDTO dto){

        try {
            service.crearUsuario(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Usuario creado");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }


    }

    //READ
    @GetMapping("/id/{uid}")
    public ResponseEntity<UsuarioRespuestaDTO>encontrarPorId(@PathVariable String uid){

        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(service.encontrarPorId(uid));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }


    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioRespuestaDTO> encontrarPorEmail(@PathVariable String email){
        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(service.encontrarPorEmail(email));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<UsuarioRespuestaDTO> encontrarPorNombre(@PathVariable String nombre){
        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(service.encontrarPorNombre(nombre));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @GetMapping("ADMIN/todoslosusuarios")
    public  ResponseEntity<List<UsuarioRespuestaDTO>> obtenerTodos(){

        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(service.obtenerTodos());

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }


    }

    //UPDATE
    @PutMapping("/actualizarusuario/{uid}")
    public ResponseEntity<String> actualizar(@PathVariable String uid, @RequestBody UsuarioActualizarDTO dto){

        try {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(service.actualizar(uid, dto));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }


    }

    @PutMapping("/actualizarpassword/{uid}")
    public ResponseEntity<String> cambiarPassword(@PathVariable String uid,@RequestBody CambioPasswordDTO dto){

        try {

            service.cambiarPassword(uid, dto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Contraseña actualizada");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }


    }

    //DELETE
    @DeleteMapping("/eliminarusuario/{uid}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String uid){

        try {

            service.eliminarUsuario(uid);

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Usuario eliminado");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }


    }
}
