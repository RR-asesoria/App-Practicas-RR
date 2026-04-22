package org.gestoriarr.appgestoriarr.controller;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestoriarr.appgestoriarr.dto.CambioPasswordDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioActualizarDTO;
import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService service;

    //TEST
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("Zona ADMIN");
    }

    //CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crearusuario")
    public ResponseEntity<String> crearUsuario(@Valid @RequestBody UsuarioCreacionDTO dto) throws FirebaseAuthException {
            service.crearUsuario(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Usuario creado");
    }

    //READ
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{uid}")
    public ResponseEntity<?> encontrarPorId(@PathVariable String uid) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.encontrarPorId(uid));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<?> encontrarPorEmail(@PathVariable String email) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.encontrarPorEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> encontrarPorNombre(@PathVariable String nombre) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.encontrarPorNombre(nombre));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/todoslosusuarios")
    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.obtenerTodos());
    }

    //UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{correo}/actualizarusuario")
    public ResponseEntity<String> AdminActualizarUsuario(
            @PathVariable String correo,
            @Valid @RequestBody UsuarioActualizarDTO dto) throws Exception {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.AdminActualizarUsuario(correo, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{correo}/password")
    public ResponseEntity<String> adminCambiarPassword(
            @PathVariable String correo,
            @Valid @RequestBody CambioPasswordDTO dto) {

        try {
            service.cambiarPasswordAdmin(correo, dto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Contraseña actualizada por admin");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('USERBASE', 'ADMIN')")
    @PutMapping("/users/actualizarusuario")
    public ResponseEntity<String> actualizar(@Valid @RequestBody UsuarioActualizarDTO dto) {
        try {

            final String uid = (String) Objects.requireNonNull(SecurityContextHolder
                            .getContext()
                            .getAuthentication())
                    .getPrincipal();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.actualizar(uid, dto));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USERBASE') or hasRole('ADMIN')")
    @PutMapping("/users/me/password")
    public ResponseEntity<String> cambiarMiPassword(
            @Valid @RequestBody CambioPasswordDTO dto) {
            final String uid = (String) Objects.requireNonNull(SecurityContextHolder
                            .getContext()
                            .getAuthentication())
                    .getPrincipal();

            service.cambiarPasswordUser(uid, dto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Contraseña actualizada");

    }

    //DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarusuario/{uid}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String uid) {
            service.eliminarUsuario(uid);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
