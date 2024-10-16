package ioc.dam.meteoevents.util;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    // Mapa concurrent per emmagatzemar tokens actius: token -> data d'expiració
    private Map<String, Date> activeTokens = new ConcurrentHashMap<>();

    // Afegir un token a la llista de tokens actius
    public void addToken(String token, Date expirationDate) {
        activeTokens.put(token, expirationDate);
        System.out.println(activeTokens);
    }

    // Verificar si un token està actiu i no ha expirat
    public boolean isTokenActive(String token) {
        Date expirationDate = activeTokens.get(token);
        if (expirationDate == null || expirationDate.before(new Date())) {
            return false;  // Token no encontrado o expirado
        }
        return true;
    }

    // Eliminar el token (logout)
    public void removeToken(String token) {
        activeTokens.remove(token);
    }
}

