package ioc.dam.meteoevents.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitat per gestionar els tokens JWT (JSON Web Token).
 * Aquesta classe proporciona mètodes per generar, validar i extreure informació dels tokens JWT.
 *
 * @author Generat amb IA (ChatGPT)
 * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
 */
@Component
public class JwtUtil {

    // Clau secreta per signar els tokens
    private String SECRET_KEY = "meva_clau";

    // Temps d'expiració del token (3 hores)
    private long expirationTime = 10800000;

    @Autowired
    private TokenManager tokenManager;

    /**
     * Genera un token JWT per a un usuari donat.
     * El token es genera amb un conjunt de claims (afirmacions) i un subject (nom d'usuari),
     * i es registra en el TokenManager.
     *
     * @param nomUsuari El nom de l'usuari per al qual es genera el token.
     * @return El token JWT generat.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    public String generarToken(String nomUsuari) {
        Map<String, Object> claims = new HashMap<>();
        String token = crearToken(claims, nomUsuari);
        // Afegir el token generat a la llista de tokens actius
        tokenManager.addToken(token, new Date(System.currentTimeMillis() + expirationTime));
        return token;
    }

    /**
     * Crea un token JWT amb claims personalitzats i un subject (nom d'usuari).
     *
     * @param claims Afirmacions que es volen incloure dins del token.
     * @param subject El nom d'usuari que es vol associar amb el token.
     * @return El token JWT creat.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    private String crearToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Valida un token JWT comparant-lo amb el nom d'usuari i verificant la seva expiració.
     *
     * @param token El token JWT que es vol validar.
     * @param nomUsuari El nom d'usuari esperat associat al token.
     * @return {@code true} si el token és vàlid, {@code false} altrament.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    public Boolean validarToken(String token, String nomUsuari) {
        final String tokenNomUsuari = extreureNomUsuari(token);
        return (tokenNomUsuari.equals(nomUsuari) && !isTokenExpired(token));
    }

    /**
     * Extreu el nom d'usuari del token JWT.
     *
     * @param token El token del qual es vol extreure el nom d'usuari.
     * @return El nom d'usuari associat amb el token.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    public String extreureNomUsuari(String token) {
        return extreureClaim(token, Claims::getSubject);
    }

    /**
     * Extreu la data d'expiració del token JWT.
     *
     * @param token El token del qual es vol extreure la data d'expiració.
     * @return La data d'expiració del token.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    public Date extreureExpiracio(String token) {
        return extreureClaim(token, Claims::getExpiration);
    }

    /**
     * Extreu una afirmació (claim) específica d'un token JWT.
     *
     * @param <T> El tipus de la dada que es vol extreure.
     * @param token El token del qual es vol extreure l'afirmació.
     * @param claimsResoldre Funció que indica quina afirmació s'ha d'extreure.
     * @return El valor de l'afirmació extreta.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    public <T> T extreureClaim(String token, java.util.function.Function<Claims, T> claimsResoldre) {
        final Claims claims = extreureTotsClaims(token);
        return claimsResoldre.apply(claims);
    }

    /**
     * Extreu totes les afirmacions (claims) d'un token JWT.
     *
     * @param token El token del qual es vol extreure les afirmacions.
     * @return Les afirmacions del token com a objecte {@code Claims}.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    private Claims extreureTotsClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * Verifica si un token JWT ha expirat.
     *
     * @param token El token que es vol comprovar.
     * @return {@code true} si el token ha expirat, {@code false} altrament.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    private Boolean isTokenExpired(String token) {
        return extreureExpiracio(token).before(new Date());
    }
}
