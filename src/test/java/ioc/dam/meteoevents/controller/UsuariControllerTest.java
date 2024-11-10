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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Proves d'integració per UsuariController.
 * Aquesta classe prova el controlador com una unitat completa amb MockMvc,
 * verificant el login, logout, la generació de tokens i la resta d'endpoints d'Usuari (llistar, afegir, eliminar, editar)
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
        String nomUsuari = "admin";

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

    /**
     * Prova del mètode que llista tots els usuaris.
     * Verifica que l'endpoint retorni una llista d'usuaris en format JSON amb un codi de resposta 200.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void llistarUsuaris() throws Exception {
        String token = "mockedJwtToken";
        String nomUsuari = "admin";

        Usuari usuari1 = new Usuari();
        usuari1.setNomUsuari("admin");
        usuari1.setFuncional_id("ADM");

        Usuari usuari2 = new Usuari();
        usuari2.setNomUsuari("convidat");
        usuari2.setFuncional_id("USR");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        List<Usuari> usuaris = Arrays.asList(usuari1, usuari2);

        when(usuariService.llistarUsuaris()).thenReturn(usuaris);

        mockMvc.perform(get("/api/usuaris")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nomUsuari").value("admin"))
                .andExpect(jsonPath("$[1].nomUsuari").value("convidat"));
    }

    /**
     * Prova del mètode que elimina un usuari.
     * Verifica que l'endpoint retorni un codi de resposta 200 i un missatge d'èxit si l'eliminació té èxit.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void eliminarUsuari() throws Exception {
        // Dades del test: identificador de l'usuari a eliminar i token de prova
        Long idUsuariEliminar = 4L;
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configuració del comportament de mock del servei
        when(usuariService.eliminarUsuari(idUsuariEliminar)).thenReturn(true);

        // Crida a l'endpoint simulada i verificació
        mockMvc.perform(delete("/api/usuaris/{id}", idUsuariEliminar)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * Prova del mètode que afegeix un nou usuari.
     * Verifica que l'endpoint retorni un codi de resposta 201 i el JSON de l'usuari creat quan es crea un usuari amb èxit.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void afegirUsuari() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Dades test
        Usuari usuariNou = new Usuari();
        usuariNou.setNomUsuari("nouUsuari");
        usuariNou.setContrasenya("contrasenyaUsuari");
        usuariNou.setFuncional_id("USR");
        usuariNou.setEmail("nou@ioc.com");
        usuariNou.setNom_c("Test mockMvc");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        when(usuariService.afegirUsuari(any(Usuari.class))).thenReturn(usuariNou);

        mockMvc.perform(post("/api/usuaris")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomUsuari\":\"nouUsuari\",\"contrasenya\":\"contrasenyaUsuari\",\"nom_c\":\"Test mockMvc\",\"email\":\"nou@ioc.com\",\"funcional_id\":\"USR\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nomUsuari").value("nouUsuari"))
                .andExpect(jsonPath("$.email").value("nou@ioc.com"))
                .andExpect(jsonPath("$.funcional_id").value("USR"));
    }

    /**
     * Prova d'integració del mètode modificarUsuari.
     * Verifica que el mètode actualitzi correctament un usuari i retorni un codi de resposta 200.
     * També comprova casos d'error com l'usuari no trobat o un token invàlid.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     */
    @Test
    void modificarUsuari_usuariTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Dades test
        Long idUsuari = 3L;

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Realitzar la petició PUT
        mockMvc.perform(put("/api/usuaris/{id}", idUsuari)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomUsuari\":\"nouUsuari_act\",\"contrasenya\":\"contrasenyaUsuari_act\",\"nom_c\":\"Test mockMvc Actualitzat\",\"email\":\"nou@ioc.com\",\"funcional_id\":\"USR\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuari actualitzat correctament."));
    }

    /**
     * Prova del mètode que cerca un usuari pel seu Id.
     * Verifica que l'endpoint retorni l'usuari especificat en format JSON amb un codi de resposta 200,
     * incloent la verificació de l'encapçalament d'autorització.
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void obtenirUsuariPerId_usuariTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Crear l'usuari simulat que retornarà el servei
        Usuari usuari = new Usuari();
        usuari.setId(1L);
        usuari.setNomUsuari("admin");
        usuari.setNom_c("Administrador");
        usuari.setFuncional_id("ADM");

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configurar el comportament del servei mock
        when(usuariService.obtenirUsuariPerId(1L)).thenReturn(Optional.of(usuari));

        // Simular la crida GET amb el token d'autenticació
        mockMvc.perform(get("/api/usuaris/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomUsuari").value("admin"))
                .andExpect(jsonPath("$.nom_c").value("Administrador"))
                .andExpect(jsonPath("$.funcional_id").value("ADM"));
    }

    /**
     * Prova del mètode que cerca un usuari pel seu Id. S'espera que no trobi l'usuari.
     * Verifica que l'endpoint retorni un codi de resposta 404
     *
     * @throws Exception en cas d'error en la prova d'integració.
     * @autor rhospital
     */
    @Test
    void obtenirUsuariPerId_usuariNoTrobat() throws Exception {
        // Dades activació token
        String nomUsuari = "admin";
        String token = "mockedJwtToken";

        // Simular que extraguem el nom d'usuari correctament
        when(jwtUtil.extreureNomUsuari(token)).thenReturn(nomUsuari);

        // Simular que el token es valid i està actiu
        when(jwtUtil.validarToken(token, nomUsuari)).thenReturn(true);
        when(tokenManager.isTokenActive(token)).thenReturn(true);

        // Configurar el servei per retornar un Optional buit
        when(usuariService.obtenirUsuariPerId(anyLong())).thenReturn(Optional.empty());

        // Simular la crida GET amb el token d'autenticació
        mockMvc.perform(get("/api/usuaris/{id}", 101L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
