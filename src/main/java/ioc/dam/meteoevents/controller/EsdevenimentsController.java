package ioc.dam.meteoevents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ioc.dam.meteoevents.aemet.AemetService;
import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.entity.Mesura;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.EsdevenimentsService;
import ioc.dam.meteoevents.util.CipherUtil;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST per a la gestió dels esdeveniments
 * Aquest controlador gestiona les peticions HTTP relacionades amb els esdeveniments
 * El controlador és l'intermediari entre les aplicacions clients i la lògica de l'apliació (Service)
 *
 * @author rhospital
 */
@RestController
@RequestMapping("/api/esdeveniments")
public class EsdevenimentsController {
    @Autowired
    private EsdevenimentsService esdevenimentsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AemetService aemetService;


    /**
     * Endpoint per obtenir la llista de tots els esdeveniments.
     *
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return una llista d'objectes {@link Esdeveniment} amb tots els esdeveniments emmagatzemats.
     * @author rhospital
     */
    @GetMapping
    public ResponseEntity<String> llistarEsdeveniments(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    List<Esdeveniment> esdeveniments = esdevenimentsService.llistarEsdeveniments();

                    // Convertim la llista d'esdeveniments a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(esdeveniments);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //token invàlid o inactiu
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); //token no proporcionat
    }


    /**
     * Endpoint per obtenir un esdeveniment específic per identificador.
     *
     * @param id l'identificador únic de l'esdeveniment que es vol obtenir.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb l'esdeveniment si es troba, o un estat HTTP 404 si no es troba.
     * @author rhospital
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> obtenirEsdevenimentPerId(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    ResponseEntity<Esdeveniment> esdeveniment = esdevenimentsService.obtenirEsdevenimentPerId(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());

                    if (esdeveniment.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment no trobat");
                    }

                    // convertim la llista d'esdeveniments a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(esdeveniment);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    //token invàlid o inactiu
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


    /**
     * Endpoint per afegir un nou esdeveniment.
     *
     * @param encryptedEsdeveniment l'objecte {@link Esdeveniment} amb les dades del nou esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb el nou esdeveniment creat i l'estat HTTP 201 si s'ha creat correctament,
     * o un estat HTTP 401 si el token és invàlid.
     * @author rhospital
     */
    @PostMapping
    public ResponseEntity<Esdeveniment> afegirEsdeveniment(@RequestBody String encryptedEsdeveniment, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Desencriptem esdeveniment enviat en el cos de la petició
                    String esdevenimentJSON = CipherUtil.decrypt(encryptedEsdeveniment);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Esdeveniment esdeveniment = objectMapper.readValue(esdevenimentJSON, Esdeveniment.class);

                    // Afegim l'esdeveniment
                    Esdeveniment nouEsdeveniment = esdevenimentsService.afegirEsdeveniment(esdeveniment);
                    return ResponseEntity.status(HttpStatus.CREATED).body(nouEsdeveniment);
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            } catch (Exception e) {
                System.out.println(e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    /**
     * Endpoint per modificar les dades d'un esdeveniment existent.
     *
     * @param id l'identificador únic de l'esdeveniment que es vol modificar.
     * @param encryptedEsdeveniment l'objecte {@link Esdeveniment} amb les noves dades de l'esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb l'esdeveniment actualitzat.
     * @author rhospital
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarEsdeveniment(
            @PathVariable Integer id,
            @RequestBody String encryptedEsdeveniment,
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
                    // Desencriptem esdeveniment enviat en el cos de la petició
                    String esdevenimentJSON = CipherUtil.decrypt(encryptedEsdeveniment);

                    // Passem el JSON desencriptat a un objecte Usuari
                    ObjectMapper objectMapper = new ObjectMapper();
                    Esdeveniment esdevenimentDetalls = objectMapper.readValue(esdevenimentJSON, Esdeveniment.class);

                    // Modifiquem l'usuari
                    Esdeveniment esdevenimentModificat = esdevenimentsService.modificarEsdeveniment(id, esdevenimentDetalls);
                    if (esdevenimentModificat != null) {
                        return ResponseEntity.ok("Esdeveniment actualitzat correctament.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment no trobat.");
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
     * Endpoint per eliminar un esdeveniment de la base de dades.
     *
     * @param id l'identificador únic de l'esdeveniment que es vol eliminar.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit si l'esdeveniment s'ha eliminat, o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEsdevenimentPerId(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desencriptar el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar que el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    boolean eliminat = esdevenimentsService.eliminarEsdeveniment(id);

                    if (eliminat) {
                        return ResponseEntity.ok("Esdeveniment eliminat amb èxit.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment no trobat.");
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
     * Endpoint per obtenir els usuaris assignats a un esdeveniment.
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment del qual es volen conèixer els usuaris assignats.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @GetMapping("/{id}/usuaris")
    public ResponseEntity<String> obtenirUsuarisPerEsdeveniment(@PathVariable("id") Integer idEsdeveniment,
                                                                      @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // mètode per obtenir llista usuaris
                    List<Usuari> usuaris = esdevenimentsService.obtenirUsuarisPerEsdeveniment(idEsdeveniment);

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
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat");
    }

    /**
     * Endpoint per obtenir les mesures de seguretat assignades a un esdeveniment.
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment del qual es volen conèixer les mesures assignades.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @GetMapping("/{id}/mesures")
    public ResponseEntity<String> obtenirMesuresPerEsdeveniment(@PathVariable("id") Integer idEsdeveniment,
                                                                      @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // mètode per obtenir llista mesures
                    List<Mesura> mesures = esdevenimentsService.obtenirMesuresPerEsdeveniment(idEsdeveniment);

                    // Convertim la llista d'usuaris a JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(mesures);

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(jsonData);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }

        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat");
    }

    /**
     * Endpoint per afegir un Usuari a un Esdeveniment.
     *
     * @param idEsdeveniment l'identificador de l'esdeveniment al qual s'ha d'afegir l'usuari.
     * @param idUsuari l'identificador de l'ususari que s'ha d'afegir a l'esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @PostMapping("/{idEsdeveniment}/usuaris/{idUsuari}")
    public ResponseEntity<String> afegirUsuariAEsdeveniment(
            @PathVariable Integer idEsdeveniment,
            @PathVariable Long idUsuari,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // Validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    boolean afegitCorrectament = esdevenimentsService.afegirUsuariAEsdeveniment(idEsdeveniment, idUsuari);

                    if (afegitCorrectament) {
                        return ResponseEntity.ok("Usuari afegit correctament a l'esdeveniment.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment o Usuari no trobats.");
                    }
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }

    /**
     * Endpoint per afegir una Mesura a un Esdeveniment.
     *
     * @param idEsdeveniment l'identificador de l'esdeveniment al qual s'ha d'afegir la mesura.
     * @param idMesura l'identificador de la mesura que s'ha d'afegir a l'esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @PostMapping("/{idEsdeveniment}/mesures/{idMesura}")
    public ResponseEntity<String> afegirMesuraAEsdeveniment(
            @PathVariable Integer idEsdeveniment,
            @PathVariable Integer idMesura,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // Validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    boolean afegitCorrectament = esdevenimentsService.afegirMesuraAEsdeveniment(idEsdeveniment, idMesura);

                    if (afegitCorrectament) {
                        return ResponseEntity.ok("Mesura afegida correctament a l'esdeveniment.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment o Mesura no trobats.");
                    }
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }

    /**
     * Endpoint per eliminar un usuari assignat a un esdeveniment.
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment.
     * @param idUsuari l'identificador únic de l'usuari que es vol eliminar de l'esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rh
     */
    @DeleteMapping("/{idEsdeveniment}/usuaris/{idUsuari}")
    public ResponseEntity<String> eliminarUsuariAssignatEsdeveniment(
            @PathVariable("idEsdeveniment") Integer idEsdeveniment,
            @PathVariable("idUsuari") Long idUsuari,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // Validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Crida al servei per eliminar l'usuari assignat a l'esdeveniment
                    boolean eliminada = esdevenimentsService.eliminarUsuariAssignatEsdeveniment(idEsdeveniment, idUsuari);

                    if (eliminada) {
                        return ResponseEntity.ok("Usuari eliminat correctament de l'esdeveniment.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("No s'ha trobat l'usuari o no està assignat a l'esdeveniment.");
                    }
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }

    /**
     * Endpoint per eliminar una mesura assignada a un esdeveniment.
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment.
     * @param idMesura l'identificador únic de la mesura que es vol eliminar de l'esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rh
     */
    @DeleteMapping("/{idEsdeveniment}/mesures/{idMesura}")
    public ResponseEntity<String> eliminarMesuraAssignadaEsdeveniment(
            @PathVariable("idEsdeveniment") Integer idEsdeveniment,
            @PathVariable("idMesura") Integer idMesura,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // Validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // Crida al servei per eliminar la mesura assignada a l'esdeveniment
                    boolean eliminada = esdevenimentsService.eliminarMesuraAssignadaEsdeveniment(idEsdeveniment, idMesura);

                    if (eliminada) {
                        return ResponseEntity.ok("Mesura eliminada correctament de l'esdeveniment.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("No s'ha trobat la mesura o no està assignada a l'esdeveniment.");
                    }
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desxifrar o processar el token");
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }

    /**
     * Endpoint per calcular la previsió meteorològica i mesures de seguretat.
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment del qual es volen conèixer les mesures assignades.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb un missatge d'èxit o un estat HTTP adequat en cas d'error.
     * @author rhospital
     */
    @GetMapping("/{id}/meteo")
    public ResponseEntity<String> calcularMeteo(@PathVariable("id") Integer idEsdeveniment,
                                                                @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token amb CipherUtil
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    // mètode per obtenir el càlcul de la meteo i mesures a prendre
                    String meteo = aemetService.calcularMeteo(idEsdeveniment);

                    // Comprobem que no hi hagi cap error
                    if (meteo.startsWith("Error")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(meteo);
                    }

                    // Xifrem el JSON amb AES per enviar-lo al client
                    String encryptedData = CipherUtil.encrypt(meteo);

                    return ResponseEntity.ok(encryptedData);
                } else {
                    // Token invàlid o inactiu
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
            }

        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat");
    }


}
