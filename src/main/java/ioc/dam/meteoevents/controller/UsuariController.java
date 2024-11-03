package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.UsuariService;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

            String nomUsuari = jwtUtil.extreureNomUsuari(token);
            tokenManager.isTokenActive(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                // Eliminar el token de la memòria
                tokenManager.removeToken(token);
                tokenManager.isTokenActive(token);
                return ResponseEntity.ok("Logout amb èxit");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("token no proporcionat");
    }

    /**
     * Endpoint per obtenir la llista de tots els usuaris.
     *
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return una llista d'objectes {@link Usuari} amb tots els usuaris emmagatzemats.
     * @author rhospital
     */
    @GetMapping
    public ResponseEntity<List<Usuari>> llistarUsuaris(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                List<Usuari> usuaris = usuariService.llistarUsuaris();
                return ResponseEntity.ok(usuaris);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //token invàlid o inactiu
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); //token no proporcionat
    }

    /**
     * Endpoint per obtenir un usuari específic per identificador.
     *
     * @param id l'identificador únic de l'usuari que es vol obtenir.
     * @return un {@link ResponseEntity} amb l'usuari si es troba, o un estat HTTP 404 si no es troba.
     * @author rhospital
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuari> obtenirUsuariPerId(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                return usuariService.obtenirUsuariPerId(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
                //.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //token invàlid o inactiu
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); //token no proporcionat
    }

    /**
     * Endpoint per afegir un nou usuari.
     *
     * @param usuari l'objecte {@link Usuari} amb les dades del nou usuari.
     * @return l'objecte {@link Usuari} que s'ha afegit a la base de dades.
     * @author rhospital
     */
    /*@PostMapping
    public Usuari afegirUsuari(@RequestBody Usuari usuari) {
        return usuariService.afegirUsuari(usuari);
    }*/

    /**
     * Endpoint per modificar les dades d'un usuari existent.
     *
     * @param id l'identificador únic de l'usuari que es vol modificar.
     * @param usuariDetalls l'objecte {@link Usuari} amb les noves dades de l'usuari.
     * @return un {@link ResponseEntity} amb l'usuari actualitzat.
     * @author rhospital
     */
    /*@PutMapping("/{id}")
    public ResponseEntity<Usuari> modificarUsuari(@PathVariable Long id, @RequestBody Usuari usuariDetalls) {
        return ResponseEntity.ok(usuariService.modificarUsuari(id, usuariDetalls));
    }*/

    /**
     * Endpoint per eliminar un usuari de la base de dades.
     *
     * @param id l'identificador únic de l'usuari que es vol eliminar.
     * @return un {@link ResponseEntity} amb l'estat HTTP 204 (No Content) si l'operació és satisfactòria.
     * @author rhospital
     */
    /*@DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuari(@PathVariable Long id) {
        usuariService.eliminarUsuari(id);
        return ResponseEntity.noContent().build();
    }*/

    /**
     * Classe auxiliar per a la resposta del token JWT.
     * Aquesta classe encapsula la informació del token JWT i el funcional_id de l'usuari.
     * @author Generat amb IA (ChatGPT).
     * @prompt "Implementar que l'ususari, al fer login, no només obtingui el token sinó també el perfil d'usuari. Implementar-ho a la classe UsuariController"
     * @author rhospital
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
        public String getFuncionalId() {
            return funcionalId;
        }

        /**
         * Estableix el funcionalId de l'usuari.
         *
         * @param funcionalId el funcionalId de l'usuari.
         * @author rhospital
         */
        public void setFuncionalId(String funcionalId) {
            this.funcionalId = funcionalId;
        }

    }
}
