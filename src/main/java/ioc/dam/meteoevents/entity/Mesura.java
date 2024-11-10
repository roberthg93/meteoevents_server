package ioc.dam.meteoevents.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entitat que representa una mesura de prevenció.
 *
 * @autor rhospital
 */
@Entity
@Table(name = "mesures")
@Getter
@Setter
public class Mesura {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mesures_seq")
    @SequenceGenerator(name = "mesures_seq", sequenceName = "mesures_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "condicio", nullable = true, length = 50)
    private String condicio;

    @Column(name = "valor", nullable = true)
    private Double valor;

    @Column(name = "valor_um", nullable = true, length = 50)
    private String valorUm;

    @Column(name = "accio", nullable = true)
    private String accio;

    // Relació amb la taula mesures_esdeveniments
    //@OneToMany(mappedBy = "mesura")
    //private List<MesuraEsdeveniment> mesuresEsdeveniments;
}
