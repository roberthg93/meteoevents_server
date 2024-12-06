package ioc.dam.meteoevents.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entitat que representa la llista de municipis d'Espanya juntament amb el seu codi, necessari per les peticions
 * a l'API de AEMET
 *
 * @autor rhospital
 */
@Entity
@Table(name = "municipi")
@Getter
@Setter
public class Municipi {

    @Id
    @Column(name = "codi", nullable = false)
    private String codi;

    @Column(name = "municipi")
    private String municipi;
}
