package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Mesura;
import ioc.dam.meteoevents.service.MesuraService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Proves d'integració per MesuraController.
 * Aquesta classe prova el controlador com una unitat completa amb MockMvc,
 * verificant el funcionament esperat dels endpoints (llistar, afegir, eliminar, editar)
 *
 * @author rhospital
 */
@SpringBootTest
@AutoConfigureMockMvc
class MesuraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MesuraService mesuraService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TokenManager tokenManager;

    /**
     * Prova del mètode que llista totes les mesures.
     * Verifica que l'endpoint retorni una llista de mesures en format JSON amb un codi de resposta 200.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void llistarMesures() throws Exception {
        String token = "mockedJwtToken";
        String nomUsuari = "admin";

        Mesura mesura1 = new Mesura();
        mesura1.setCondicio("vent");
        mesura1.setValor(30.0);

        Mesura mesura2 = new Mesura();
        mesura2.setCondicio("vent");
        mesura2.setValor(40.0);

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        List<Mesura> mesures = Arrays.asList(mesura1, mesura2);

        when(mesuraService.llistarMesures()).thenReturn(mesures);

        mockMvc.perform(get("/api/mesures")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].valor").value("30.0"))
                .andExpect(jsonPath("$[1].valor").value("40.0"));
    }

    /**
     * Prova del mètode que elimina una mesura.
     * Verifica que l'endpoint retorni un codi de resposta 200 i un missatge d'èxit si l'eliminació té èxit.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void eliminarMesura() throws Exception {
        // Dades del test: identificador de l'mesura a eliminar i token de prova
        Integer idMesuraEliminar = 4;
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configuració del comportament de mock del servei
        when(mesuraService.eliminarMesura(idMesuraEliminar)).thenReturn(true);

        // Crida a l'endpoint simulada i verificació
        mockMvc.perform(delete("/api/mesures/{id}", idMesuraEliminar)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * Prova del mètode que afegeix una nova mesura.
     * Verifica que l'endpoint retorni un codi de resposta 201 i el JSON de la mesura creada quan es crea una mesura amb èxit.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void afegirMesura() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Dades test
        Mesura mesuraNou = new Mesura();
        mesuraNou.setCondicio("temperatura");
        mesuraNou.setValor(40.0);
        mesuraNou.setValorUm("ºC");
        mesuraNou.setAccio("cancelar esdeveniment");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        when(mesuraService.afegirMesura(any(Mesura.class))).thenReturn(mesuraNou);

        mockMvc.perform(post("/api/mesures")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"condicio\":\"temperatura\",\"valor\":\"40.0\"," +
                                "\"valorUm\":\"ºC\",\"accio\":\"cancelar esdeveniment\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.condicio").value("temperatura"))
                .andExpect(jsonPath("$.valor").value("40.0"))
                .andExpect(jsonPath("$.valorUm").value("ºC"))
                .andExpect(jsonPath("$.accio").value("cancelar esdeveniment"));
    }

    /**
     * Prova d'integració del mètode modificarMesura.
     * Verifica que el mètode actualitzi correctament una mesura i retorni un codi de resposta 200.
     * També comprova casos d'error com la mesura no trobada o un token invàlid.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     */
    @Test
    void modificarMesura_mesuraTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Dades test
        Integer idMesura = 3;

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Realitzar la petició PUT
        mockMvc.perform(put("/api/mesures/{id}", idMesura)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"condicio\":\"precipitacio\",\"valor\":\"20.0\"," +
                                "\"valorUm\":\"mm/h\",\"accio\":\"informació i comunicació als usuaris, revisió desguassos i embornals\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mesura actualitzada correctament."));
    }

    /**
     * Prova del mètode que cerca una mesura pel seu Id.
     * Verifica que l'endpoint retorni la mesura especificada en format JSON amb un codi de resposta 200,
     * incloent la verificació de l'encapçalament d'autorització.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void obtenirMesuraPerId_mesuraTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Crear l'mesura simulat que retornarà el servei
        Mesura mesura = new Mesura();
        mesura.setId(1);
        mesura.setCondicio("IV The Traka");
        mesura.setValorUm("km/h");
        mesura.setAccio("desmontar pancartes, fixar escenari");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configurar el comportament del servei mock
        when(mesuraService.obtenirMesuraPerId(1)).thenReturn(Optional.of(mesura));

        // Simular la crida GET amb el token d'autenticació
        mockMvc.perform(get("/api/mesures/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.condicio").value("vent"))
                .andExpect(jsonPath("$.valorUm").value("km/h"))
                .andExpect(jsonPath("$.accio").value("desmontar pancartes, fixar escenari"));
    }
}
