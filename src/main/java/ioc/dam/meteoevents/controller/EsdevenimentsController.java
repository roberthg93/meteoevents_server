package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.entity.Mesura;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.EsdevenimentsService;
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


    /**
     * Endpoint per obtenir la llista de tots els esdeveniments.
     *
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return una llista d'objectes {@link Esdeveniment} amb tots els esdeveniments emmagatzemats.
     * @author rhospital
     */
    @GetMapping
    public ResponseEntity<List<Esdeveniment>> llistarEsdeveniments(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                List<Esdeveniment> esdeveniments = esdevenimentsService.llistarEsdeveniments();
                return ResponseEntity.ok(esdeveniments);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //token invàlid o inactiu
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
    public ResponseEntity<Esdeveniment> obtenirEsdevenimentPerId(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                return esdevenimentsService.obtenirEsdevenimentPerId(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
            }
            //token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        //token no proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    /**
     * Endpoint per afegir un nou esdeveniment.
     *
     * @param esdeveniment l'objecte {@link Esdeveniment} amb les dades del nou esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb el nou esdeveniment creat i l'estat HTTP 201 si s'ha creat correctament,
     * o un estat HTTP 401 si el token és invàlid.
     * @author rhospital
     */
    @PostMapping
    public ResponseEntity<Esdeveniment> afegirEsdeveniment(@RequestBody Esdeveniment esdeveniment, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                Esdeveniment nouEsdeveniment = esdevenimentsService.afegirEsdeveniment(esdeveniment);
                return ResponseEntity.status(HttpStatus.CREATED).body(nouEsdeveniment);
            } else {
                // Token invàlid o inactiu
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    /**
     * Endpoint per modificar les dades d'un esdeveniment existent.
     *
     * @param id l'identificador únic de l'esdeveniment que es vol modificar.
     * @param esdevenimentDetalls l'objecte {@link Esdeveniment} amb les noves dades de l'esdeveniment.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb l'esdeveniment actualitzat.
     * @author rhospital
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarEsdeveniment(
            @PathVariable Integer id,
            @RequestBody Esdeveniment esdevenimentDetalls,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Comprova que el token JWT està present i és vàlid
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                Esdeveniment esdevenimentModificat = esdevenimentsService.modificarEsdeveniment(id, esdevenimentDetalls);
                if (esdevenimentModificat != null) {
                    return ResponseEntity.ok("Esdeveniment actualitzat correctament.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment no trobat.");
                }
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar que el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                boolean eliminat = esdevenimentsService.eliminarEsdeveniment(id);

                if (eliminat) {
                    return ResponseEntity.ok("Esdeveniment eliminat amb èxit.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment no trobat.");
                }
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
    public ResponseEntity<List<Usuari>> obtenirUsuarisPerEsdeveniment(@PathVariable("id") Integer idEsdeveniment,
                                                                      @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                // mètode per obtenir llista usuaris
                List<Usuari> usuaris = esdevenimentsService.obtenirUsuarisPerEsdeveniment(idEsdeveniment);
                return ResponseEntity.ok(usuaris);
            } else {
                // Token invàlid o inactiu
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
    public ResponseEntity<List<Mesura>> obtenirMesuresPerEsdeveniment(@PathVariable("id") Integer idEsdeveniment,
                                                                      @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                // mètode per obtenir llista mesures
                List<Mesura> mesures = esdevenimentsService.obtenirMesuresPerEsdeveniment(idEsdeveniment);
                return ResponseEntity.ok(mesures);
            } else {
                // Token invàlid o inactiu
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // Validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                boolean afegitCorrectament = esdevenimentsService.afegirUsuariAEsdeveniment(idEsdeveniment, idUsuari);

                if (afegitCorrectament) {
                    return ResponseEntity.ok("Usuari afegit correctament a l'esdeveniment.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment o Usuari no trobats.");
                }
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // Validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                boolean afegitCorrectament = esdevenimentsService.afegirMesuraAEsdeveniment(idEsdeveniment, idMesura);

                if (afegitCorrectament) {
                    return ResponseEntity.ok("Mesura afegida correctament a l'esdeveniment.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esdeveniment o Mesura no trobats.");
                }
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
            String token = authorizationHeader.substring(7);
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
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
            String token = authorizationHeader.substring(7);
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
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionat.");
    }


}
