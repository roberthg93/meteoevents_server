package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.util.CipherUtil;
import ioc.dam.meteoevents.util.JwtUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Proves d'integració per MunicipiController.
 * Aquesta classe prova el controlador com una unitat completa amb MockMvc,
 * verificant el funcionament esperat de l'endpoint d'obtenir codi de municipi
 *
 * @author rhospital
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MunicipiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TokenManager tokenManager;


    /**
     * Prova del mètode que cerca un codi de municipi pel seu nom
     * Verifica que l'endpoint retorni un codi en format String amb un codi de resposta 200,
     * incloent la verificació de l'encapçalament d'autorització.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void obtenirCodiMunicipi_Trobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        token = CipherUtil.encrypt(token);

        // Simular la crida GET amb el token d'autenticació
        mockMvc.perform(get("/api/municipis/{municipi}", "Sant Joan de Mollet")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(result -> {
                    // Obtenir el cos de la resposta
                    String encryptedResponseBody = result.getResponse().getContentAsString();

                    // Desxifrar la resposta
                    String decryptedResponseBody = CipherUtil.decrypt(encryptedResponseBody);

                    // Exemple d'asserts
                    assertEquals("17168", decryptedResponseBody);
                });
    }

    /**
     * Prova del mètode que cerca un codi de municipi pel seu nom
     * Verifica que l'endpoint retorni un codi en format String amb el missatge "Municipi no trobat" i
     * codi de resposta 404, incloent la verificació de l'encapçalament d'autorització.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void obtenirCodiMunicipi_NoTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        token = CipherUtil.encrypt(token);

        // Simular la crida GET amb el token d'autenticació
        mockMvc.perform(get("/api/municipis/{municipi}", "Sant Esteve de les Roures")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Municipi no trobat"));
    }
}
