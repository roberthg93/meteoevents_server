package ioc.dam.meteoevents.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe que representa un esdeveniment en el sistema.
 *
 * Aquesta entitat s'utilitza per mapejar els esdeveniments de la base de dades.
 * Cada esdeveniment té un identificador únic, un nom, descripció, organitzador,
 * adreça, codi postal, població, aforament i horari.
 *
 * S'utilitza JPA per a la persistència de dades i Lombok per generar automàticament
 * els getters i setters.
 *
 * @author rhospital
 */
@Entity
@Table(name = "esdeveniments")
@Getter
@Setter
public class Esdeveniment {

    /**
     * Identificador únic de l'esdeveniment (clau primària).
     * Generat automàticament mitjançant l'estratègia de seqüència personalitzada.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esdeveniments_seq")
    @SequenceGenerator(name = "esdeveniments_seq", sequenceName = "esdeveniments_seq", allocationSize = 1)
    private Integer id;

    /**
     * Nom de l'esdeveniment.
     */
    @Column(nullable = false)
    private String nom;

    /**
     * Descripció de l'esdeveniment.
     */
    @Column
    private String descripcio;

    /**
     * Organitzador de l'esdeveniment.
     */
    @Column
    private String organitzador;

    /**
     * Adreça de l'esdeveniment.
     */
    @Column
    private String direccio;

    /**
     * Codi postal de l'esdeveniment.
     */
    @Column(name = "codi_postal")
    private String codi_postal;

    /**
     * Població on es realitza l'esdeveniment.
     */
    @Column
    private String poblacio;

    /**
     * Aforament de l'esdeveniment.
     */
    @Column
    private String aforament;

    /**
     * Horari inici de l'esdeveniment.
     */
    @Column(name = "hora_inici")
    private String hora_inici;

    /**
     * Horari fi de l'esdeveniment.
     */
    @Column(name = "hora_fi")
    private String hora_fi;

    /**
     * Data de l'esdeveniment.
     */
    @Column(name = "data_esde")
    private String data_esde;
}
