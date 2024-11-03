package ioc.dam.meteoevents.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Classe que representa un usuari (Usuari) en el sistema.
 *
 * Aquesta entitat s'utilitza per mapejar els usuaris registrats a la base de dades.
 * Cada usuari té un identificador únic, un nom complet, un nom d'usuari,
 * una contrasenya, un identificador funcional i la seva última connexió.
 *
 * S'utilitza JPA per a la persistència de dades i Lombok per generar automàticament
 * els getters i setters.
 *
 * La classe implementa userDetails perquè es pugui integrar amb Spring Security
 *
 * @author rhospital
 */


@Entity
@Table(name = "usuaris")
@Getter
@Setter

public class Usuari implements UserDetails {
    /**
     * Identificador únic de l'usuari (clau primària).
     * Generat automàticament mitjançant l'estratègia d'identificació IDENTITY.
     *
     * @author rhospital
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom complet de l'usuari.
     * Aquest camp és obligatori (no pot ser nul).
     *
     * @author rhospital
     */
    @Column(nullable = false)
    private String nom_c;

    /**
     * Identificador funcional de l'usuari.
     * Aquest camp és obligatori (no pot ser nul).
     *
     * @author rhospital
     */
    @Column(nullable = false)
    private String funcional_id;

    /**
     * Nom d'usuari utilitzat per iniciar sessió.
     * Aquest camp és obligatori, no pot ser nul i ha de ser únic.
     *
     * @author rhospital
     */
    @Column(name = "nom_usuari", nullable = false, unique = true)
    private String nomUsuari;

    /**
     * Contrasenya de l'usuari.
     * Aquest camp és obligatori (no pot ser nul).
     *
     * @author rhospital
     */
    @Column(nullable = false)
    private String contrasenya;

    /**
     * Data de l'última connexió de l'usuari al sistema.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private String ultima_connexio;

    /**
     * Data naixement usuari.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private String data_naixement;

    /**
     * Sexe de l'usuari.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private String sexe;

    /**
     * Població de l'usuari.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private String poblacio;

    /**
     * Correu electrònic de l'usuari.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private String email;

    /**
     * Número de telèfon de l'usuari.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private Long telefon;

    /**
     * Descripció de l'usuari.
     * Aquest camp és opcional (pot ser nul).
     *
     * @author rhospital
     */
    @Column
    private String descripcio;


    /**
     * Obté la contrasenya de l'usuari.
     *
     * @return la contrasenya de l'usuari.
     *
     * @author rhospital
     */
    public String getContrasenya() {
        return contrasenya;
    }

    /**
     * Obté el nom d'usuari (nom per iniciar sessió).
     *
     * @return el nom d'usuari.
     *
     * @author rhospital
     */
    public String getNomUsuari() {
        return nomUsuari;
    }

    /**
     * Obté l'identificador funcional de l'usuari.
     *
     * @return l'identificador funcional de l'usuari.
     *
     * @author rhospital
     */
    public String getFuncional_id() {
        return funcional_id;
    }

    /**
     * Obté el nom d'usuari (nom per iniciar sessió). Mètode obligatori per implementar el getAuthorities de UserDetails
     *
     * @return el nom d'usuari.
     *
     * @author rhospital
     */
    @Override
    public String getUsername() {
        return nomUsuari;
    }

    /**
     * Obté la contrasenya de l'usuari. Mètode obligatori per implementar el getAuthorities de UserDetails.
     *
     * @return la contrasenya de l'usuari.
     *
     * @author rhospital
     */
    @Override
    public String getPassword() {
        return contrasenya;
    }

    /**
     * Obtén les autoritzacions de l'usuari.
     * Mètode necessari per integrar l'Spring Security i poder implementar el UserDetails satisfactoriament
     * Aquest mètode mira quins rols tenen autorització per poder fer peticions. En el nostre cas no restringim a rols d'usuari
     *
     * @return Una col·lecció d'autoritzacions que permeten l'accés general.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna una col·lecció amb una autorització per defecte
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

}
