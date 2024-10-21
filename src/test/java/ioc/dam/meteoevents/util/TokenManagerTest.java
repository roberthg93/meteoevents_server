package ioc.dam.meteoevents.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test per a la classe {@link TokenManager}.
 * Es prova el comportament d'afegir, eliminar i verificar si un token és actiu.
 *
 * @author rhospital
 */
public class TokenManagerTest {

    private TokenManager tokenManager;
    private String testToken;
    private Date validExpirationDate;
    private Date expiredDate;

    @BeforeEach
    public void setUp() {
        tokenManager = new TokenManager();
        testToken = "testToken";
        validExpirationDate = new Date(System.currentTimeMillis() + 10000); // 10 segons de validesa
        expiredDate = new Date(System.currentTimeMillis() - 10000); // Ja està expirat
    }

    /**
     * Test per verificar que un token es pot afegir i es considera actiu.
     *
     * @author rhospital
     */
    @Test
    public void testAddToken() {
        tokenManager.addToken(testToken, validExpirationDate);
        assertTrue(tokenManager.isTokenActive(testToken), "El token hauria de ser actiu.");
    }

    /**
     * Test per verificar que un token expirat no es considera actiu.
     *
     * @author rhospital
     */
    @Test
    public void testTokenExpirat() {
        tokenManager.addToken(testToken, expiredDate);
        assertFalse(tokenManager.isTokenActive(testToken), "El token hauria d'estar expirat.");
    }

    /**
     * Test per verificar que un token es pot eliminar.
     *
     * @author rhospital
     */
    @Test
    public void testRemoveToken() {
        tokenManager.addToken(testToken, validExpirationDate);
        tokenManager.removeToken(testToken);
        assertFalse(tokenManager.isTokenActive(testToken), "El token no hauria de ser actiu després d'eliminar-lo.");
    }
}
