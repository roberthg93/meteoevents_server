package ioc.dam.meteoevents.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe que representa la clau composta per a l'entitat {@link EsdevenimentUsuari}.
 * Defineix els camps que conformen la clau composta: idEsdeveniment i idUsuari.
 *
 * Aquesta classe implementa la interfície Serializable perquè JPA requereix que les claus compostes siguin serialitzables.
 *
 * @autor rhospital
 */
public class EsdevenimentUsuariId implements Serializable {

    private Long idEsdeveniment;
    private Long idUsuari;

    public EsdevenimentUsuariId() {
    }

    public EsdevenimentUsuariId(Long idEsdeveniment, Long idUsuari) {
        this.idEsdeveniment = idEsdeveniment;
        this.idUsuari = idUsuari;
    }

    // Getters i Setters
    public Long getIdEsdeveniment() {
        return idEsdeveniment;
    }

    public void setIdEsdeveniment(Long idEsdeveniment) {
        this.idEsdeveniment = idEsdeveniment;
    }

    public Long getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(Long idUsuari) {
        this.idUsuari = idUsuari;
    }

    // Equals i hashCode per identificar correctament les instàncies
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EsdevenimentUsuariId that = (EsdevenimentUsuariId) o;
        return Objects.equals(idEsdeveniment, that.idEsdeveniment) &&
                Objects.equals(idUsuari, that.idUsuari);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEsdeveniment, idUsuari);
    }
}
