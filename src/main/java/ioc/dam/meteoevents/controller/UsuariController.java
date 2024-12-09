package ioc.dam.meteoevents.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.UsuariService;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import ioc.dam.meteoevents.util.CipherUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Base64;

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



    /*@PostMapping("/login")
    public ResponseEntity<String> loginUsuari(@RequestBody String encryptedRequest) {
        try {
            // Desxifra la petició
            String decryptedRequest = cipherUtil.decrypt(encryptedRequest);
            String[] parts = decryptedRequest.split(":");
            String nomUsuari = parts[0];
            String contrasenya = parts[1];

            Usuari usuari = usuariService.autenticar(nomUsuari, contrasenya);

            if (usuari != null) {
                String token = jwtUtil.generarToken(nomUsuari);

                // Preparem la resposta en format JSON
                String responseJson = String.format("{\"token\": \"%s\", \"funcional_id\": \"%s\", \"id\": \"%d\"}",
                        token, usuari.getFuncional_id(), usuari.getId());

                // Xifrem la resposta
                String encryptedResponse = cipherUtil.encrypt(responseJson);
                return ResponseEntity.ok(encryptedResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Tornarà un error 401 sense cos
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en processar la sol·licitud.");
        }
    }*/

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
    public ResponseEntity<String> loginUsuari(@RequestParam String nomUsuari, @RequestParam String contrasenya) {
        try {
            System.out.println(Instant.now());
            String contrasenyaLogin = "admin24" + "|" + String.valueOf(Instant.now());
            String contrasenyaCiph = CipherUtil.encrypt(contrasenyaLogin);
            String contrasenyaCipBase = Base64.getEncoder().encodeToString(contrasenyaCiph.getBytes());
            System.out.println(contrasenyaCipBase);
            // Descodificar el nom d'Usuari amb Base64
            byte[] nomUsuariBytes = Base64.getDecoder().decode(nomUsuari);
            String encryptedNomUsuariCipher = new String(nomUsuariBytes);

            // Descodificar contrasenya encriptada amb Cipher
            String nomUsuariTextPla = CipherUtil.decrypt(encryptedNomUsuariCipher);

            // Descodificar la contrasenya amb Base64
            byte[] contrasenyaBytes = Base64.getDecoder().decode(contrasenya);
            String encryptedContrasenyaCipher = new String(contrasenyaBytes);

            // Descodificar contrasenya encriptada amb Cipher
            String contrasenyaTextPla = CipherUtil.decrypt(encryptedContrasenyaCipher);

            // Per seguretat, la contrasenya ve concatenada amb un Timestamp del moment que s'ha sol·licitat el login
            String[] parts = contrasenyaTextPla.split("\\|");
            if (parts.length != 2) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Format invalid. Assegura't d'enviar la contarsenya " +
                        "concatenada així 'contarsenya|timestamp'");
            }

            String contrasenya_separada = parts[0];
            String timestampString = parts[1];

            // Comprobem que el timestamp adjuntat amb la contrasenya no sigui superior a fa 30 minuts
            Instant timestamp = Instant.parse(timestampString);
            Duration duration = Duration.between(timestamp, Instant.now());

            if (duration.toMinutes() > 30) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contrasenya invàlida, han transcorregut més de " +
                        "30 minuts des de la sol·licitud");
            }

            Usuari usuari = usuariService.autenticar(nomUsuariTextPla, contrasenya_separada);

            if (usuari != null) {
                String token = jwtUtil.generarToken(nomUsuariTextPla);
                JwtResponse jwtResponse = new JwtResponse(token, usuari.getFuncional_id(), usuari.getId());

                // Serialitzar l'objecte JwtResponse a JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(jwtResponse);

                // Print intern per saber el valor del token encriptat i utilitzar-lo per fer proves
                System.out.println(CipherUtil.encrypt(token));
                System.out.println(token);

                // Xifrar la resposta JSON
                String encryptedResponse = CipherUtil.encrypt(jsonResponse);

                return ResponseEntity.ok(encryptedResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Tornarà un error 401 sense cos
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la descodificació de la contrasenya");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al xifrar la resposta");
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
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                tokenManager.isTokenActive(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Eliminar el token de la memòria
                    tokenManager.removeToken(token);
                    tokenManager.isTokenActive(token);
                    return ResponseEntity.ok("Logout amb èxit");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
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
    public ResponseEntity<String> llistarUsuaris(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7); // Token encriptat

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);

                // Extreiem el nom d'usuari del token desxifrat
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // Validem el token i comprovem si és actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Obtenim la llista d'usuaris
                    List<Usuari> usuaris = usuariService.llistarUsuaris();

                    // Convertim la llista d'usuaris a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(usuaris);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu");
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no està correctament format");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat");
    }


    /**
     * Endpoint per obtenir un usuari específic per identificador.
     *
     * @param id l'identificador únic de l'usuari que es vol obtenir.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb l'usuari si es troba, o un estat HTTP 404 si no es troba.
     * @author rhospital
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> obtenirUsuariPerId(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token
                String token = CipherUtil.decrypt(encryptedToken);

                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    ResponseEntity<Usuari> usuari = usuariService.obtenirUsuariPerId(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                    //.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

                    // Convertim la llista d'usuaris a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Exclou els valors null
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY); // Exclou valors buits
                    String jsonData = objectMapper.writeValueAsString(usuari);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token no està correctament format");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        //token no proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    /*@GetMapping("/{id}")
    public ResponseEntity<Usuari> obtenirUsuariPerId(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token
                String token = CipherUtil.decrypt(encryptedToken);

                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    return usuariService.obtenirUsuariPerId(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                    //.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                }
                //token invàlid o inactiu
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }   catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        //token no proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }*/

    /**
     * Endpoint per afegir un nou usuari.
     *
     * @param encryptedUsuari l'objecte {@link Usuari} encriptat i en forma de String amb les dades del nou usuari.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb el nou usuari creat i l'estat HTTP 201 si s'ha creat correctament,
     * o un estat HTTP 401 si el token és invàlid.
     * @author rhospital
     */
    @PostMapping
    public ResponseEntity<Usuari> afegirUsuari(@RequestBody String encryptedUsuari, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Desencriptem usuari enviat en el cos de la petició
                    String usuariJSON = CipherUtil.decrypt(encryptedUsuari);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Usuari usuari = objectMapper.readValue(usuariJSON, Usuari.class);
                    // Afegim l'usuari
                    Usuari nouUsuari = usuariService.afegirUsuari(usuari);

                    return ResponseEntity.status(HttpStatus.CREATED).body(nouUsuari);
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            } catch (Exception e) {
                System.out.println("ERROR USER INSERT: " + e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Endpoint per modificar les dades d'un usuari existent.
     *
     * @param id l'identificador únic de l'usuari que es vol modificar.
     * @param encryptedUsuari l'objecte {@link Usuari} amb les noves dades de l'usuari.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb l'usuari actualitzat.
     * @author rhospital
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarUsuari(
            @PathVariable Long id,
            @RequestBody String encryptedUsuari,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Comprova que el token JWT està present i és vàlid
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Desencriptem usuari enviat en el cos de la petició
                    String usuariJSON = CipherUtil.decrypt(encryptedUsuari);

                    // Passem el JSON desencriptat a un objecte Usuari
                    ObjectMapper objectMapper = new ObjectMapper();
                    Usuari usuari = objectMapper.readValue(usuariJSON, Usuari.class);

                    // Modifiquem l'usuari
                    Usuari usuariModificat = usuariService.modificarUsuari(id, usuari);
                    if (usuariModificat != null) {
                        return ResponseEntity.ok("Usuari actualitzat correctament.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
                    }
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }


    /**
     * Endpoint per eliminar un usuari de la base de dades.
     *
     * @param id l'identificador únic de l'usuari que es vol eliminar.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit si l'usuari s'ha eliminat, o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuariPerId(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar que el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    boolean eliminat = usuariService.eliminarUsuari(id);

                    if (eliminat) {
                        return ResponseEntity.ok("Usuari eliminat amb èxit.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
                    }
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }

    /**
     * Endpoint per obtenir els esdeveniments assignats a un usuari.
     *
     * @param idUsuari l'identificador únic de l'usuari del qual es volen conèixer els esdeveniments assignats.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @GetMapping("/{id}/esdeveniments")
    public ResponseEntity<String> obtenirEsdevenimentsPerUsuari(@PathVariable("id") Long idUsuari,
                                                                             @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // mètode per obtenir llista d'esdeveniments
                    List<Esdeveniment> esdeveniments = usuariService.obtenirEsdevenimentsPerUsuari(idUsuari);

                    // Convertim la llista d'esdeveniments a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(esdeveniments);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Classe auxiliar per a la resposta del token JWT.
     * Aquesta classe encapsula la informació del token JWT i el funcional_id de l'usuari.
     * @author Generat amb IA (ChatGPT).
     * @prompt "Implementar que l'ususari, al fer login, no només obtingui el token sinó també el perfil d'usuari. Implementar-ho a la classe UsuariController"
     * @author rhospital
     */
    public static class JwtResponse {
        private String token;
        private String funcionalId; // Nou camp per funcional_id
        private Long usuariId;

        // Constructor per defecte
        public JwtResponse() {
        }

        /**
         * Constructor per inicialitzar el token i el funcionalId.
         *
         * @param token el token JWT generat.
         * @param funcionalId l'identificador funcional de l'usuari.
         * @author rhospital
         */
        public JwtResponse(String token, String funcionalId, Long usuariId) {
            this.token = token;
            this.funcionalId = funcionalId;
            this.usuariId = usuariId;
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

        /**
         * Retorna el usuariId de l'usuari.
         *
         * @return el usuariId de l'usuari.
         * @author rhospital
         */
        public Long getUsuariId() {
            return usuariId;
        }

        /**
         * Estableix el usuariId de l'usuari.
         *
         * @param usuariId el funcionalId de l'usuari.
         * @author rhospital
         */
        public void setUsuariId(Long usuariId) {
            this.usuariId = usuariId;
        }

    }
}
