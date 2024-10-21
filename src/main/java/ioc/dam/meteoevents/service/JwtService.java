package ioc.dam.meteoevents.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import ioc.dam.meteoevents.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servei per gestionar les operacions relacionades amb els tokens JWT
 * i la recuperació d'usuaris associats.
 *
 * @author rhospital
 */
@Service
public class JwtService {

    private final UsuariRepository usuariRepository;
    private final JwtUtil jwtUtil;

    /**
     * Constructor del servei JwtService.
     *
     * @param usuariRepository Repositori per accedir a les dades d'usuari.
     * @param jwtUtil Utilitat per gestionar tokens JWT.
     * @author rhospital
     */
    @Autowired
    public JwtService(UsuariRepository usuariRepository, JwtUtil jwtUtil) {
        this.usuariRepository = usuariRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Extreu un usuari a partir d'un token JWT.
     * Si el token és vàlid i no ha expirat, es busca l'usuari al repositori
     * a partir del nom d'usuari extret del token.
     *
     * @param token El token JWT del qual es vol obtenir l'usuari.
     * @return Un Optional que conté l'usuari si es troba, o buit si el token és invàlid o ha expirat.
     * @author rhospital
     */
    public Optional<Usuari> getUserFromToken(String token) {
        try {
            String nomUsuari = jwtUtil.extreureNomUsuari(token);
            return usuariRepository.findByNomUsuari(nomUsuari);
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirat");
            return Optional.empty();
        } catch (JwtException e) {
            System.out.println("Token invalid");
            return Optional.empty();
        }
    }

    /**
     * Valida un token JWT comparant-lo amb el nom d'usuari.
     *
     * @param token El token JWT que es vol validar.
     * @param nomUsuari El nom d'usuari esperat associat al token.
     * @return {@code true} si el token és vàlid, {@code false} altrament.
     * @author rhospital
     */
    public boolean validarToken(String token, String nomUsuari) {
        return jwtUtil.validarToken(token, nomUsuari);
    }
}
