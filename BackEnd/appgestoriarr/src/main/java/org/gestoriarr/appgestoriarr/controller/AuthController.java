package org.gestoriarr.appgestoriarr.controller;
//TEST DE CONEXIÓN


import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {

        try {

            String uid = (String) authentication.getPrincipal();

            FirebaseAuth.getInstance().revokeRefreshTokens(uid);

            return ResponseEntity.ok("Logout correcto");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al hacer logout");
        }

    }

}
