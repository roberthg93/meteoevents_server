package ioc.dam.meteoevents.util;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Gestor de tokens actius.
 * Aquesta classe s'encarrega d'afegir, verificar i eliminar tokens actius
 * en una estructura de dades concurrent. Utilitza un mapa per emmagatzemar
 * els tokens i les seves dates d'expiració.
 *
 * @author rhospital
 */
@Component
public class TokenManager {

    // Mapa concurrent per emmagatzemar tokens actius: token -> data d'expiració
    private Map<String, Date> activeTokens = new ConcurrentHashMap<>();

    /**
     * Afegir un token a la llista de tokens actius.
     *
     * @param token El token a afegir.
     * @param expirationDate La data d'expiració del token.
     * @author rhospital
     */
    public void addToken(String token, Date expirationDate) {
        activeTokens.put(token, expirationDate);
        System.out.println(activeTokens);
    }

    /**
     * Verificar si un token està actiu i no ha expirat.
     *
     * @param token El token a verificar.
     * @return true si el token està actiu; false si no ho està o si ha expirat.
     * @author rhospital
     */
    public boolean isTokenActive(String token) {
        Date expirationDate = activeTokens.get(token);
        if (expirationDate == null || expirationDate.before(new Date())) {
            return false;  // Token no trobat o expirat
        }
        return true;
    }

    /**
     * Eliminar el token de la llista de tokens actius (logout).
     *
     * @param token El token a eliminar.
     * @author rhospital
     */
    public void removeToken(String token) {
        activeTokens.remove(token);
    }
}
