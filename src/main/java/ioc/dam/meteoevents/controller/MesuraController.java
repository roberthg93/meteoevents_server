package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.entity.Mesura;
import ioc.dam.meteoevents.service.MesuraService;
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

    /**
     * Endpoint per obtenir la llista de totes les mesures.
     *
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return una llista d'objectes {@link Mesura} amb totes les mesures emmagatzemats.
     * @author rhospital
     */
    @GetMapping
    public ResponseEntity<List<Mesura>> llistarMesures(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                List<Mesura> mesuras = mesuraService.llistarMesures();
                return ResponseEntity.ok(mesuras);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //token invàlid o inactiu
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
    public ResponseEntity<Mesura> obtenirMesuraPerId(@PathVariable Integer id, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                return mesuraService.obtenirMesuraPerId(id)
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
     * Endpoint per afegir una nova mesura.
     *
     * @param mesura l'objecte {@link Mesura} amb les dades de la nova mesura.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb la nova mesura creada i l'estat HTTP 201 si s'ha creat correctament,
     * o un estat HTTP 401 si el token és invàlid.
     * @author rhospital
     */
    @PostMapping
    public ResponseEntity<Mesura> afegirMesura(@RequestBody Mesura mesura, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                Mesura novaMesura = mesuraService.afegirMesura(mesura);
                return ResponseEntity.status(HttpStatus.CREATED).body(novaMesura);
            } else {
                // Token invàlid o inactiu
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Endpoint per modificar les dades d'una mesura existent.
     *
     * @param id l'identificador únic de la mesura que es vol modificar.
     * @param mesuraDetalls l'objecte {@link Mesura} amb les noves dades de la mesura.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb la mesura actualitzada.
     * @author rhospital
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> modificarMesura(
            @PathVariable Integer id,
            @RequestBody Mesura mesuraDetalls,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Comprova que el token JWT està present i és vàlid
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                Mesura mesuraModificada = mesuraService.modificarMesura(id, mesuraDetalls);
                if (mesuraModificada != null) {
                    return ResponseEntity.ok("Mesura actualitzada correctament.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesura no trobada.");
                }
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar que el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                boolean eliminat = mesuraService.eliminarMesura(id);

                if (eliminat) {
                    return ResponseEntity.ok("Mesura eliminada amb èxit.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesura no trobada.");
                }
            }
            // Token invàlid o inactiu
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invàlid o inactiu.");
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
    public ResponseEntity<List<Esdeveniment>> obtenirEsdevenimentsPerMesura(@PathVariable("id") Integer idMesura,
                                                                            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String nomUsuari = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                // mètode per obtenir llista d'esdeveniments
                List<Esdeveniment> esdeveniments = mesuraService.obtenirEsdevenimentsPerMesura(idMesura);
                return ResponseEntity.ok(esdeveniments);
            } else {
                // Token invàlid o inactiu
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        // Cap token proporcionat
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}
