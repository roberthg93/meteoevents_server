package ioc.dam.meteoevents.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe DTO per a l'entitat Mesura.
 *
 * Aquesta classe serveix per transferir dades entre la capa de servei i la capa de presentació.
 * Simplifica el transport d'informació amb els camps més rellevants per a les operacions actuals.
 *
 * Utilitza Lombok per generar automàticament els getters i setters.
 *
 * @author rhospital
 */
@Getter
@Setter
public class MesuraDTO {

    /**
     * Identificador únic de la mesura.
     */
    private Integer id;

    /**
     * Condició meteorològica de la mesura (ex: vent, precipitació).
     */
    private String condicio;

    /**
     * Valor associat a la condició meteorològica.
     */
    private Double valor;

    /**
     * Unitat de mesura per al valor (ex: km/h, mm/h).
     */
    private String valorUm;

    /**
     * Acció recomanada segons la condició i el valor.
     */
    private String accio;

    /**
     * Nivell mesura segons la condició i el valor.
     */
    private Integer nivellMesura;
}
