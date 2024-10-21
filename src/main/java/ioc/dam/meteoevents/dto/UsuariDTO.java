package ioc.dam.meteoevents.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe de transferència de dades (DTO) per a l'entitat Usuari.
 * S'utilitza per transferir dades entre les capes de la lògica de negoci i la presentació.
 *
 * @author rhospital
 */

@Getter
@Setter

public class UsuariDTO {
    /**
     * Identificador únic de l'usuari.
     */
    private Long id;

    /**
     * Nom d'usuari que utilitza per accedir al sistema.
     */
    private String nomUsuari;

    /**
     * Nom complet de l'usuari.
     */
    private String nom_c;
}
