package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.UsuariService;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenManager tokenManager;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUsuari(@RequestParam String nomUsuari, @RequestParam String contrasenya) {
        Usuari usuari = usuariService.autenticar(nomUsuari, contrasenya);

        if (usuari != null) {
            String token = jwtUtil.generarToken(nomUsuari);
            return ResponseEntity.ok(new JwtResponse(token, usuari.getFuncional_id()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Tornarà un error 401 sense cos
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            // Eliminar el token de la memoria
            tokenManager.removeToken(token);

            return ResponseEntity.ok("Logout amb èxit");
        }
        return ResponseEntity.badRequest().body("Token no proporcionat");
    }

    public class JwtResponse {
        private String token;
        private String funcionalId; // Nuevo campo para funcional_id

        public JwtResponse(String token, String funcionalId) {
            this.token = token;
            this.funcionalId = funcionalId; // Inicializa el nuevo campo
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getFuncionalId() { // Nuevo getter
            return funcionalId;
        }

        public void setFuncionalId(String funcionalId) { // Nuevo setter
            this.funcionalId = funcionalId;
        }
    }
}
