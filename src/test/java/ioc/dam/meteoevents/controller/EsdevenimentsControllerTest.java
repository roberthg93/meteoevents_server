package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.service.EsdevenimentsService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Proves d'integració per EsdevenimentsController.
 * Aquesta classe prova el controlador com una unitat completa amb MockMvc,
 * verificant el funcionament esperat dels endpoints (llistar, afegir, eliminar, editar)
 *
 * @author rhospital
 */
@SpringBootTest
@AutoConfigureMockMvc
class EsdevenimentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EsdevenimentsService esdevenimentsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TokenManager tokenManager;

    /**
     * Prova del mètode que llista tots els esdeveniments.
     * Verifica que l'endpoint retorni una llista d'esdeveniments en format JSON amb un codi de resposta 200.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void llistarEsdeveniments() throws Exception {
        String token = "mockedJwtToken";
        String nomUsuari = "admin";

        Esdeveniment esdev1 = new Esdeveniment();
        esdev1.setNom("IV The Traka");
        esdev1.setOrganitzador("Klassmark");

        Esdeveniment esdev2 = new Esdeveniment();
        esdev2.setNom("Festival Cruïlla");
        esdev2.setOrganitzador("Cruïlla Barcelona");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        List<Esdeveniment> esdeveniments = Arrays.asList(esdev1, esdev2);

        when(esdevenimentsService.llistarEsdeveniments()).thenReturn(esdeveniments);

        mockMvc.perform(get("/api/esdeveniments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nom").value("IV The Traka"))
                .andExpect(jsonPath("$[1].nom").value("Festival Cruïlla"));
    }

    /**
     * Prova del mètode que elimina un esdeveniment.
     * Verifica que l'endpoint retorni un codi de resposta 200 i un missatge d'èxit si l'eliminació té èxit.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void eliminarEsdeveniment() throws Exception {
        // Dades del test: identificador de l'esdeveniment a eliminar i token de prova
        Integer idEsdevenimentEliminar = 2;
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configuració del comportament de mock del servei
        when(esdevenimentsService.eliminarEsdeveniment(idEsdevenimentEliminar)).thenReturn(true);

        // Crida a l'endpoint simulada i verificació
        mockMvc.perform(delete("/api/esdeveniments/{id}", idEsdevenimentEliminar)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * Prova del mètode que afegeix un nou esdeveniment.
     * Verifica que l'endpoint retorni un codi de resposta 201 i el JSON de l'esdeveniment creat quan es crea un esdeveniment amb èxit.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void afegirEsdeveniment() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Dades test
        Esdeveniment esdevenimentNou = new Esdeveniment();
        esdevenimentNou.setNom("Campionat d'España de Surf 2024");
        esdevenimentNou.setDescripcio("Campionat d'España de Surf");
        esdevenimentNou.setOrganitzador("Federació Espanyola de Surf");
        esdevenimentNou.setDireccio("Platja de Pantín");
        esdevenimentNou.setPoblacio("Valdoviño");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        when(esdevenimentsService.afegirEsdeveniment(any(Esdeveniment.class))).thenReturn(esdevenimentNou);

        mockMvc.perform(post("/api/esdeveniments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Campionat d'España de Surf 2024\",\"descripcio\":\"Campionat d'España de Surf\"," +
                                "\"organitzador\":\"Federació Espanyola de Surf\",\"direccio\":\"Platja de Pantín\"," +
                                "\"poblacio\":\"Valdoviño\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nom").value("Campionat d'España de Surf 2024"))
                .andExpect(jsonPath("$.descripcio").value("Campionat d'España de Surf"))
                .andExpect(jsonPath("$.organitzador").value("Federació Espanyola de Surf"))
                .andExpect(jsonPath("$.direccio").value("Platja de Pantín"))
                .andExpect(jsonPath("$.poblacio").value("Valdoviño"));
    }

    /**
     * Prova d'integració del mètode modificarEsdeveniment.
     * Verifica que el mètode actualitzi correctament un esdeveniment i retorni un codi de resposta 200.
     * També comprova casos d'error com l'esdeveniment no trobat o un token invàlid.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     */
    @Test
    void modificarEsdeveniment_esdevenimentTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Dades test
        Integer idEsdeveniment = 3;

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Realitzar la petició PUT
        mockMvc.perform(put("/api/esdeveniments/{id}", idEsdeveniment)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Campionat d'España de Surf 2025\",\"descripcio\":\"Campionat d'España de Surf\"," +
                                "\"organitzador\":\"Federació Espanyola de Surf\",\"direccio\":\"Platja de Zarautz\"," +
                                "\"poblacio\":\"Zarautz\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Esdeveniment actualitzat correctament."));
    }

    /**
     * Prova del mètode que cerca un esdeveniment pel seu Id.
     * Verifica que l'endpoint retorni l'esdeveniment especificat en format JSON amb un codi de resposta 200,
     * incloent la verificació de l'encapçalament d'autorització.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void obtenirEsdevenimentPerId_esdevenimentTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Crear l'esdeveniment simulat que retornarà el servei
        Esdeveniment esdeveniment = new Esdeveniment();
        esdeveniment.setId(1);
        esdeveniment.setNom("IV The Traka");
        esdeveniment.setOrganitzador("Klassmark");
        esdeveniment.setPoblacio("Girona");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configurar el comportament del servei mock
        when(esdevenimentsService.obtenirEsdevenimentPerId(1)).thenReturn(Optional.of(esdeveniment));

        // Simular la crida GET amb el token d'autenticació
        mockMvc.perform(get("/api/esdeveniments/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("IV The Traka"))
                .andExpect(jsonPath("$.organitzador").value("Klassmark"))
                .andExpect(jsonPath("$.poblacio").value("Girona"));
    }
}
