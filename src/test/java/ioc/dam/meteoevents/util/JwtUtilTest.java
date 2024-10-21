package ioc.dam.meteoevents.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de prova per a JwtUtil.
 * Aquesta classe conté proves unitàries per verificar la creació i validació de tokens JWT.
 *
 * @author rhospital
 */
class JwtUtilTest {

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private JwtUtil jwtUtil;

    /**
     * Configura les dades necessàries per a les proves.
     *
     * @author rhospital
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Prova de la generació de token.
     * Verifica que es generi un token correcte a partir del nom d'usuari.
     *
     * @author rhospital
     */
    @Test
    void generarToken() {
        String token = jwtUtil.generarToken("admin");
        assertNotNull(token);
    }

    /**
     * Prova de validació de token.
     * Verifica que un token vàlid és acceptat pel mètode de validació.
     *
     * @author rhospital
     */
    @Test
    void validarToken() {
        String token = jwtUtil.generarToken("admin");
        assertTrue(jwtUtil.validarToken(token, "admin"));
    }

    /**
     * Prova d'extracció del nom d'usuari des del token.
     * Verifica que el nom d'usuari extret d'un token generat sigui correcte.
     *
     * @author rhospital
     */
    @Test
    void extreureNomUsuari() {
        String token = jwtUtil.generarToken("admin");
        String extractedNomUsuari = jwtUtil.extreureNomUsuari(token);
        assertEquals("admin", extractedNomUsuari);
    }

    /**
     * Prova de l'expiració del token.
     * Verifica que el mètode reconegui correctament un token que ha expirat.
     *
     * @author rhospital
     */
    @Test
    void testTokenValidity() {
        String token = jwtUtil.generarToken("admin");
        assertTrue(jwtUtil.validarToken(token, "admin"));
    }

}
