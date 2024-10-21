package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de prova per a UsuariService.
 * Aquesta classe conté proves unitàries per als mètodes de la classe UsuariService,
 * simulant les dependències amb Mockito.
 *
 * @author rhospital
 */
class UsuariServiceTest {

    @Mock
    private UsuariRepository usuariRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuariService usuariService;

    private Usuari usuari;

    /**
     * Configura les dades necessàries per les proves.
     *
     * @author rhospital
     */
    @BeforeEach
    void setUp() {
        usuari = new Usuari();
        usuari.setNomUsuari("admin");
        usuari.setContrasenya("admin24");
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Prova del mètode d'autenticació amb èxit.
     * Verifica que es retorna l'usuari correcte quan la contrasenya coincideix.
     *
     * @author rhospital
     */
    @Test
    void autenticarUsuariCorrecte() {
        when(usuariRepository.findByNomUsuari("admin")).thenReturn(Optional.of(usuari));
        when(passwordEncoder.matches("admin24", usuari.getContrasenya())).thenReturn(true);

        Usuari resultat = usuariService.autenticar("admin", "admin24");
        assertNotNull(resultat);
        assertEquals("admin", resultat.getNomUsuari());
    }

    /**
     * Prova del mètode d'autenticació quan l'usuari no es troba.
     * Verifica que el mètode retorna null si l'usuari no existeix.
     *
     * @author rhospital
     */
    @Test
    void autenticarUsuariNoTrobat() {
        when(usuariRepository.findByNomUsuari("desconegut")).thenReturn(Optional.empty());

        Usuari resultat = usuariService.autenticar("desconegut", "admin24");
        assertNull(resultat);
    }

    /**
     * Prova del mètode d'autenticació amb contrasenya incorrecta.
     * Verifica que el mètode retorna null quan la contrasenya és incorrecta.
     *
     * @author rhospital
     */
    @Test
    void autenticarContrasenyaIncorrecta() {
        when(usuariRepository.findByNomUsuari("admin")).thenReturn(Optional.of(usuari));
        when(passwordEncoder.matches("contrasenya_erronia", usuari.getContrasenya())).thenReturn(false);

        Usuari resultat = usuariService.autenticar("admin", "contrasenya_erronia");
        assertNull(resultat);
    }
}
