package ioc.dam.meteoevents.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Clau composta per a l'entitat {@link MesuraEsdeveniment}.
 *
 * Aquesta classe implementa la interfície Serializable perquè JPA requereix que les claus compostes siguin serialitzables.
 *
 * @autor rhospital
 */
@Getter
@Setter
@EqualsAndHashCode
public class MesuraEsdevenimentId implements Serializable {

    private Integer idEsdeveniment;
    private Integer idMesura;

    // Constructor per defecte
    public MesuraEsdevenimentId() {}

    // Constructor amb tots els camps
    public MesuraEsdevenimentId(Integer idEsdeveniment, Integer idMesura) {
        this.idEsdeveniment = idEsdeveniment;
        this.idMesura = idMesura;
    }
}
