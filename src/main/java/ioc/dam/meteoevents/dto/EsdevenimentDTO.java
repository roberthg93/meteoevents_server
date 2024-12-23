package ioc.dam.meteoevents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe DTO per a l'entitat Esdeveniment.
 *
 * Aquesta classe serveix per transferir dades entre la capa de servei i la capa de presentació.
 * Es diferencia de l'entitat en què està dissenyada per simplificar el transport d'informació,
 * i pot contenir només aquells camps rellevants per a l'operació actual.
 *
 * Utilitza Lombok per generar automàticament els getters i setters.
 *
 * @author rhospital
 */
@Getter
@Setter
public class EsdevenimentDTO {

    /**
     * Identificador únic de l'esdeveniment.
     */
    private Integer id;

    /**
     * Nom de l'esdeveniment.
     */
    private String nom;

    /**
     * Descripció de l'esdeveniment.
     */
    private String descripcio;

    /**
     * Organitzador de l'esdeveniment.
     */
    private String organitzador;

    /**
     * Adreça de l'esdeveniment.
     */
    private String direccio;

    /**
     * Codi postal de l'esdeveniment.
     */
    private String codi_postal;

    /**
     * Població on es realitza l'esdeveniment.
     */
    private String poblacio;

    /**
     * Aforament de l'esdeveniment.
     */
    private String aforament;

    /**
     * Horari inici de l'esdeveniment.
     */
    private String hora_inici;

    /**
     * Horari fi de l'esdeveniment.
     */
    private String hora_fi;

    /**
     * Data de l'esdeveniment.
     */
    private String data_esde;
}
