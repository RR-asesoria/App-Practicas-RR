package org.gestoriarr.appgestoriarr.controller;
//TEST DE CONEXIÓN


import org.gestoriarr.appgestoriarr.dto.UsuarioCreacionDTO;
import org.gestoriarr.appgestoriarr.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final UsuarioService service;

    public AdminController(UsuarioService service){
        this.service = service;
    }

    @GetMapping("/conexion")
    public ResponseEntity<String> conexionApiCliente(){
        return ResponseEntity.ok("Conexión con API");
    }

    @PostMapping("/usuario")
    public ResponseEntity<String> crearUsuario(UsuarioCreacionDTO dto) throws Exception {

            service.crearUsuario(dto);
            return ResponseEntity.ok("Usuario creado.");

    }

    //COMENTARIO RANDOM

}
