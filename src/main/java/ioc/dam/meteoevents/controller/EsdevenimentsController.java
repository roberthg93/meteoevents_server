package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Esdeveniment;
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
            String nomEsdeveniment = jwtUtil.extreureNomUsuari(token);

            // validar el token sigui correcte i actiu
            if (jwtUtil.validarToken(token, nomEsdeveniment) && tokenManager.isTokenActive(token)) {
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
}
