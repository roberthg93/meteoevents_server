package ioc.dam.meteoevents.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entitat que representa la relació entre {@link Esdeveniment} i {@link Usuari}.
 * Utilitza una clau composta per identificar les instàncies únicament.
 *
 * @autor rhospital
 */
@Entity
@Table(name = "esdeveniments_usuaris")
@IdClass(EsdevenimentUsuariId.class)
@Getter
@Setter
public class EsdevenimentUsuari {

    @Id
    @Column(name = "id_esdeveniment", nullable = false)
    private Long idEsdeveniment;

    @Id
    @Column(name = "id_usuari", nullable = false)
    private Long idUsuari;

    // Relacions amb Esdeveniment i Usuari
    @ManyToOne
    @JoinColumn(name = "id_esdeveniment", insertable = false, updatable = false)
    private Esdeveniment esdeveniment;

    @ManyToOne
    @JoinColumn(name = "id_usuari", insertable = false, updatable = false)
    private Usuari usuari;
}
