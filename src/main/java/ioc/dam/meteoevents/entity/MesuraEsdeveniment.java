package ioc.dam.meteoevents.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entitat que representa la relació entre {@link Esdeveniment} i {@link Mesura}.
 * Utilitza una clau composta per identificar les instàncies únicament.
 *
 * @autor rhospital
 */
@Entity
@Table(name = "mesures_esdeveniments")
@IdClass(MesuraEsdevenimentId.class)
@Getter
@Setter
public class MesuraEsdeveniment {

    @Id
    @Column(name = "id_esdeveniment", nullable = false)
    private Integer idEsdeveniment;

    @Id
    @Column(name = "id_mesura", nullable = false)
    private Integer idMesura;

    // Relacions amb Esdeveniment i Mesura
    @ManyToOne
    @JoinColumn(name = "id_esdeveniment", insertable = false, updatable = false)
    private Esdeveniment esdeveniment;

    @ManyToOne
    @JoinColumn(name = "id_mesura", insertable = false, updatable = false)
    private Mesura mesura;
}
