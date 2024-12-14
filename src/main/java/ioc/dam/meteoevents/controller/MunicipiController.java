package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.repository.MunicipiRepository;
import ioc.dam.meteoevents.util.CipherUtil;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST per a la gestió de la classe municipi
 * Aquest controlador gestiona les peticions HTTP relacionades amb els municipis
 * El controlador és l'intermediari entre les aplicacions clients i la lògica de l'apliació (Service)
 *
 * @author rhospital
 */
@RestController
@RequestMapping("/api/municipis")
public class MunicipiController {
    @Autowired
    private MunicipiRepository municipiRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenManager tokenManager;

    /**
     * Endpoint per obtenir el codi de municipi per nom de municipi.
     *
     * @param municipi el nom del municipi que es vol obtenir el codi.
     * @param authorizationHeader l'encapçalament HTTP "Authorization" que conté el token JWT.
     * @return un {@link ResponseEntity} amb l'esdeveniment si es troba, o un estat HTTP 404 si no es troba.
     * @author rhospital
     */
    @GetMapping("/{municipi}")
    public ResponseEntity<String> obtenirCodiMunicipi(@PathVariable String municipi, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedToken = authorizationHeader.substring(7);

            try {
                // Desxifrem el token
                String token = CipherUtil.decrypt(encryptedToken);
                String nomUsuari = jwtUtil.extreureNomUsuari(token);

                // validar el token sigui correcte i actiu
                if (jwtUtil.validarToken(token, nomUsuari) && tokenManager.isTokenActive(token)) {
                    String municipiCodi = municipiRepository.findCodiByMunicipi(municipi);

                    if (municipiCodi != null) {
                        String encryptedMunicipiCodi = CipherUtil.encrypt(municipiCodi);
                        return ResponseEntity.ok(encryptedMunicipiCodi);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Municipi no trobat");
                    }
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
}
