package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import ioc.dam.meteoevents.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Classe de test per a la classe {@link JwtService}.
 * Es prova el comportament del servei en relació amb l'extracció i validació de tokens.
 *
 * @author rhospital
 */
public class JwtServiceTest {

    @Mock
    private UsuariRepository usuariRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtService jwtService;

    private String validToken;
    private Usuari usuari;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validToken = "validToken";
        usuari = new Usuari();
        usuari.setNomUsuari("testUser");
    }

    /**
     * Test per verificar que es retorna l'usuari correcte des del token.
     *
     * @author rhospital
     */
    @Test
    public void testGetUserFromToken() {
        when(jwtUtil.extreureNomUsuari(validToken)).thenReturn("testUser");
        when(usuariRepository.findByNomUsuari("testUser")).thenReturn(Optional.of(usuari));

        Optional<Usuari> result = jwtService.getUserFromToken(validToken);
        assertTrue(result.isPresent(), "L'usuari hauria d'estar present.");
        assertEquals("testUser", result.get().getNomUsuari(), "El nom d'usuari hauria de coincidir.");
    }

    /**
     * Test per validar un token amb un nom d'usuari correcte.
     *
     * @author rhospital
     */
    @Test
    public void testValidarToken() {
        when(jwtUtil.validarToken(validToken, "testUser")).thenReturn(true);

        boolean isValid = jwtService.validarToken(validToken, "testUser");
        assertTrue(isValid, "El token hauria de ser vàlid.");
    }
}
