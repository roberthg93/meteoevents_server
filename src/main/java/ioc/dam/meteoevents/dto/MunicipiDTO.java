package ioc.dam.meteoevents.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe DTO per a l'entitat Municipi.
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
public class MunicipiDTO {
    /**
     * Identificador únic del municipi.
     */
    private String codi;

    /**
     * Nom del municipi
     */
    private String municipi;
}
