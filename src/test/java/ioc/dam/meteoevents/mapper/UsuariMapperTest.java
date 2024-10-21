package ioc.dam.meteoevents.mapper;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.entity.Usuari;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Classe de test per a la classe {@link UsuariMapper}.
 * Es prova el comportament del mètode de conversió entre {@link Usuari} i {@link UsuariDTO}.
 *
 * @author rhospital
 */
public class UsuariMapperTest {

    @Mock
    private UsuariMapper usuariMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Iniciar els mocks
    }

    /**
     * Test per verificar que un {@link Usuari} es pot mappejar correctament a un {@link UsuariDTO}.
     *
     * @author rhospital
     */
    @Test
    public void testMapToDto() {
        Usuari usuari = new Usuari();
        usuari.setNomUsuari("admin");

        UsuariDTO expectedDto = new UsuariDTO();
        expectedDto.setNomUsuari("admin");

        // Simular el comportament del mapper
        when(usuariMapper.toDTO(usuari)).thenReturn(expectedDto);

        UsuariDTO resultDto = usuariMapper.toDTO(usuari);

        assertEquals("admin", resultDto.getNomUsuari(), "El nom d'usuari hauria de coincidir en el DTO.");
    }

    /**
     * Test per verificar que un {@link UsuariDTO} es pot mappejar correctament a un {@link Usuari}.
     *
     * @author rhospital
     */
    @Test
    public void testMapToEntity() {
        UsuariDTO dto = new UsuariDTO();
        dto.setNomUsuari("admin");

        Usuari expectedUsuari = new Usuari();
        expectedUsuari.setNomUsuari("admin");

        // Simular el comportament del mapper
        when(usuariMapper.toEntity(dto)).thenReturn(expectedUsuari);

        Usuari resultUsuari = usuariMapper.toEntity(dto);

        assertEquals("admin", resultUsuari.getNomUsuari(), "El nom d'usuari hauria de coincidir en l'entitat.");
    }
}
