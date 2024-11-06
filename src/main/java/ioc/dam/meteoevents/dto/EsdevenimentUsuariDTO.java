package ioc.dam.meteoevents.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe DTO per a l'entitat EsdevenimentUsuari.
 *
 * Aquesta classe serveix per transferir dades entre la capa de servei i la capa de presentació.
 * Conté només aquells camps necessaris per representar la relació entre esdeveniments i usuaris.
 *
 * Utilitza Lombok per generar automàticament els getters i setters.
 *
 * @author rhospital
 */
@Getter
@Setter
public class EsdevenimentUsuariDTO {

    /**
     * Identificador de l'esdeveniment.
     */
    private Integer idEsdeveniment;

    /**
     * Identificador de l'usuari.
     */
    private Integer idUsuari;
}
