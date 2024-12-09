package ioc.dam.meteoevents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.entity.Mesura;
import ioc.dam.meteoevents.service.MesuraService;
import ioc.dam.meteoevents.util.CipherUtil;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST per a la gestió de les mesures
 * Aquest controlador gestiona les peticions HTTP relacionades amb les mesures
 * El controlador és l'intermediari entre les aplicacions clients i la lògica de l'apliació (Service)
 *
 * @author rhospital
 */
@RestController
@RequestMapping("/api/mesures")
public class MesuraController {
    @Autowired
    private MesuraService mesuraService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenManager tokenManager;
    private List<Esdeveniment> esdeveniments;

    /**
     * Endpoint per obtenir la llista de totes les mesures.
     *
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return una llista d'objectes {@link Mesura} amb totes les mesures emmagatzemats.
     * @author rhospital
     */
    @GetMapping
    public ResponseEntity<String> llistarMesures(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    List<Mesura> mesures = mesuraService.llistarMesures();

                    // Convertim la llista de mesures a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(mesures);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //token invàlid o inactiu
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); //token no proporcionat
    }

    /**
     * Endpoint per obtenir una mesura específica per identificador.
     *
     * @param id l'identificador únic de la mesura que es vol obtenir.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb la mesura si es troba, o un estat HTTP 404 si no es troba.
     * @author rhospital
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> obtenirMesuraPerId(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    ResponseEntity<Mesura> mesura = mesuraService.obtenirMesuraPerId(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());

                    // convertim la llista d'esdeveniments a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(mesura);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    //token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        //token no proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Endpoint per afegir una nova mesura.
     *
     * @param encryptedMesura l'objecte {@link Mesura} amb les dades de la nova mesura.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb la nova mesura creada i l'estat HTTP 201 si s'ha creat correctament,
     * o un estat HTTP 401 si el token és invàlid.
     * @author rhospital
     */
    @PostMapping
    public ResponseEntity<Mesura> afegirMesura(@RequestBody String encryptedMesura, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Desencriptem mesura enviada en el cos de la petició
                    String mesuraJSON = CipherUtil.decrypt(encryptedMesura);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Mesura mesura = objectMapper.readValue(mesuraJSON, Mesura.class);

                    // Afegim la mesura
                    Mesura novaMesura = mesuraService.afegirMesura(mesura);
                    return ResponseEntity.status(HttpStatus.CREATED).body(novaMesura);
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
     * Endpoint per modificar les dades d'una mesura existent.
     *
     * @param id l'identificador únic de la mesura que es vol modificar.
     * @param encryptedMesura l'objecte {@link Mesura} amb les noves dades de la mesura.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb la mesura actualitzada.
     * @author rhospital
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarMesura(
            @PathVariable Integer id,
            @RequestBody String encryptedMesura,
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
                    // Desencriptem mesura enviada en el cos de la petició
                    String mesuraJSON = CipherUtil.decrypt(encryptedMesura);

                    // Passem el JSON desencriptat a un objecte Usuari
                    ObjectMapper objectMapper = new ObjectMapper();
                    Mesura mesuraDetalls = objectMapper.readValue(mesuraJSON, Mesura.class);

                    // Modifiquem l'usuari
                    Mesura mesuraModificada = mesuraService.modificarMesura(id, mesuraDetalls);
                    if (mesuraModificada != null) {
                        return ResponseEntity.ok("Mesura actualitzada correctament.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesura no trobada.");
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
     * Endpoint per eliminar una mesura de la base de dades.
     *
     * @param id l'identificador únic de la mesura que es vol eliminar.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit si la mesura s'ha eliminat, o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMesuraPerId(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar que el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    boolean eliminat = mesuraService.eliminarMesura(id);

                    if (eliminat) {
                        return ResponseEntity.ok("Mesura eliminada amb èxit.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesura no trobada.");
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
     * Endpoint per obtenir els esdeveniments assignats a una mesura de seguretat
     *
     * @param idMesura l'identificador únic de la mesura de la qual es volen conèixer els esdeveniments assignats.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @GetMapping("/{id}/esdeveniments")
    public ResponseEntity<String> obtenirEsdevenimentsPerMesura(@PathVariable("id") Integer idMesura,
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
                    List<Esdeveniment> esdeveniments = mesuraService.obtenirEsdevenimentsPerMesura(idMesura);

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
}
