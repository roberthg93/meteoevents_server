package ioc.dam.meteoevents.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe DTO per a l'entitat MesuraEsdeveniment.
 *
 * Aquesta classe serveix per transferir dades de l'associació entre Mesura i Esdeveniment
 * entre la capa de servei i la capa de presentació.
 *
 * Utilitza Lombok per generar automàticament els getters i setters.
 *
 * @author rhospital
 */
@Getter
@Setter
public class MesuraEsdevenimentDTO {

    /**
     * Identificador de l'esdeveniment associat a la mesura.
     */
    private Integer idEsdeveniment;

    /**
     * Identificador de la mesura associada a l'esdeveniment.
     */
    private Integer idMesura;
}
