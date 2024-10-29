package ioc.dam.meteoevents.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de prova d'integració per validar la funcionalitat de la base de dades d'usuaris.
 *
 * @author rhospital
 */
@ExtendWith(SpringExtension.class) // Habilita Spring Extension amb JUnit 5
@SpringBootTest // Carrega el context complet de Spring per fer proves d'integració.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Utilitza la base de dades real
@Transactional // Garanteix que les transaccions siguin revertides després de cada test
public class UsuariRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Prova d'integració que comprova si es poden inserir dades d'usuari i recuperar-les correctament de la base de dades.
     */
    @Test
    public void testInsertAndRecuperacioUsuari() {
        // Insereix un nou usuari a la base de dades
        jdbcTemplate.update("INSERT INTO usuaris (nom_c, funcional_id, nom_usuari, contrasenya) VALUES (?, ?, ?, ?)",
                "Prova Usuari", "USR", "usuari_prova", "pass_prova");

        // Recupera tots els usuaris de la base de dades
        List<Map<String, Object>> usuaris = jdbcTemplate.queryForList("SELECT * FROM usuaris");

        // Comprova que hi ha almenys 3 usuaris (els dos de la inserció inicial i el que acabem d'inserir)
        assertThat(usuaris).hasSizeGreaterThanOrEqualTo(3);

        // Comprova que l'usuari inserit té les dades correctes
        Map<String, Object> usuari = usuaris.stream()
                .filter(u -> "usuari_prova".equals(u.get("nom_usuari")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Usuari no trobat"));

        assertThat(usuari.get("nom_c")).isEqualTo("Prova Usuari");
        assertThat(usuari.get("funcional_id")).isEqualTo("USR");
        assertThat(usuari.get("contrasenya")).isEqualTo("pass_prova");
    }

    /**
     * Prova d'integració per comprovar la recuperació d'usuaris administradors.
     */
    @Test
    public void testRecupearcioAdminUser() {
        // Recupera l'usuari administrador de la base de dades
        List<Map<String, Object>> admins = jdbcTemplate.queryForList("SELECT * FROM usuaris WHERE funcional_id = ?", "ADM");

        // Comprova que hi ha almenys un administrador
        assertThat(admins).isNotEmpty();

        // Comprova que l'usuari administrador té el nom d'usuari "admin"
        Map<String, Object> admin = admins.stream()
                .filter(a -> "admin".equals(a.get("nom_usuari")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Administrador no trobat"));

        assertThat(admin.get("nom_usuari")).isEqualTo("admin");
    }
}
