package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.UsuariService;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Proves d'integració per UsuariController.
 * Aquesta classe prova el controlador com una unitat completa amb MockMvc,
 * verificant el login, logout i la generació de tokens.
 *
 * @author rhospital
 */
@SpringBootTest
@AutoConfigureMockMvc
class UsuariControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UsuariService usuariService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TokenManager tokenManager;

    /**
     * Prova d'integració del mètode de login.
     * Verifica que el login retorni un token JWT quan l'usuari i la contrasenya són correctes.
     *
     * @author rhospital
     */
    @Test
    void loginUsuariCorrecte() throws Exception {
        Usuari usuari = new Usuari();
        usuari.setNomUsuari("admin");
        usuari.setFuncional_id("ADM");

        // Mockejem els mètodes per retornar valors de prova
        when(usuariService.autenticar("admin", "admin24")).thenReturn(usuari);
        when(jwtUtil.generarToken("admin")).thenReturn("mockedJwtToken"); // El mock retorna el token esperat

        // Simulació de la petició POST per al login
        mockMvc.perform(post("/api/usuaris/login")
                        .param("nomUsuari", "admin")
                        .param("contrasenya", "admin24")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockedJwtToken"))
                .andExpect(jsonPath("$.funcionalId").value("ADM"));
    }

    /**
     * Prova d'integració del mètode de login quan la contrasenya és incorrecta.
     * Verifica que es retorni un error 401 si l'autenticació falla.
     *
     * @author rhospital
     */
    @Test
    void loginUsuariContrasenyaIncorrecta() throws Exception {
        when(usuariService.autenticar("admin", "contrasenya_erronia")).thenReturn(null);

        mockMvc.perform(post("/api/usuaris/login")
                        .param("nomUsuari", "admin")
                        .param("contrasenya", "contrasenya_erronia")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Prova d'integració del mètode logout.
     * Verifica que el logout s'executi correctament i retorni un missatge d'èxit.
     *
     * @author rhospital
     */
    @Test
    void logoutUsuariCorrecte() throws Exception {
        String token = "mockedJwtToken";
        String nomUsuari = "mockedUsuari";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Simular que el mètode removeToken no fa res, ja que és void
        doNothing().when(tokenManager).removeToken(token);

        // Executar la petició de logout i fer les assertions
        mockMvc.perform(post("/api/usuaris/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout amb èxit"));
    }
}
