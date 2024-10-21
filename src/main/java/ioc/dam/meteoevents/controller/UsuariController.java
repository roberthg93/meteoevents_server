package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.UsuariService;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per a la gestió dels usuaris, incloent operacions d'inici de sessió i tancament de sessió.
 * Aquest controlador gestiona les peticions HTTP relacionades amb els usuaris, com ara login i logout.
 * El controlador és l'intermediari entre les aplicacions clients i la lògica de l'apliació (Service)
 *
 * @author rhospital
 */
@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenManager tokenManager;


    /**
     * Endpoint per a l'inici de sessió d'un usuari.
     * Aquest mètode rep el nom d'usuari i la contrasenya, valida les credencials i retorna un token JWT si l'autenticació és correcta.
     *
     * @param nomUsuari el nom d'usuari del client.
     * @param contrasenya la contrasenya de l'usuari.
     * @return un {@link ResponseEntity} amb el token JWT si l'autenticació és exitosa, o un error 401 si les credencials són incorrectes.
     * @author rhospital
     */
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

    /**
     * Endpoint per al tancament de sessió d'un usuari.
     * Aquest mètode elimina el token JWT de la memòria per invalidar la sessió de l'usuari.
     *
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} indicant l'èxit del logout o un missatge d'error si no es proporciona el token.
     * @author rhospital
     */
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

    /**
     * Classe auxiliar per a la resposta del token JWT.
     * Aquesta classe encapsula la informació del token JWT i el funcional_id de l'usuari.
     * @author Generat amb IA (ChatGPT).
     * @prompt "Implementar que l'ususari, al fer login, no només obtingui el token sinó també el perfil d'usuari. Implementar-ho a la classe UsuariController"
     */
    public class JwtResponse {
        private String token;
        private String funcionalId; // Nou camp per funcional_id

        /**
         * Constructor per inicialitzar el token i el funcionalId.
         *
         * @param token el token JWT generat.
         * @param funcionalId l'identificador funcional de l'usuari.
         * @author rhospital
         */
        public JwtResponse(String token, String funcionalId) {
            this.token = token;
            this.funcionalId = funcionalId; // Inicializa el nuevo campo
        }

        /**
         * Retorna el token JWT.
         *
         * @return el token JWT.
         * @author rhospital
         */
        public String getToken() {
            return token;
        }

        /**
         * Estableix el token JWT.
         *
         * @param token el token JWT.
         * @author rhospital
         */
        public void setToken(String token) {
            this.token = token;
        }

        /**
         * Retorna el funcionalId de l'usuari.
         *
         * @return el funcionalId de l'usuari.
         * @author rhospital
         */
        public String getFuncionalId() { // Nuevo getter
            return funcionalId;
        }

        /**
         * Estableix el funcionalId de l'usuari.
         *
         * @param funcionalId el funcionalId de l'usuari.
         * @author rhospital
         */
        public void setFuncionalId(String funcionalId) { // Nuevo setter
            this.funcionalId = funcionalId;
        }
    }
}
