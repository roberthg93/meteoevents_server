package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.Esdeveniment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositori per gestionar les operacions de persistència per a l'entitat Esdeveniment.
 * Proporciona mètodes per accedir als esdeveniments a la base de dades.
 *
 * Utilitza Spring Data JPA per facilitar la interacció amb la base de dades.
 *
 * @author rhospital
 */
@Repository
public interface EsdevenimentRepository extends JpaRepository<Esdeveniment, Integer> {

    /**
     * Troba tots els esdeveniments d'una població específica.
     *
     * @param poblacio nom de la població.
     * @return llista d'Esdeveniment en la població especificada.
     */
    List<Esdeveniment> findByPoblacio(String poblacio);

    /**
     * Troba tots els esdeveniments organitzats per un organitzador específic.
     *
     * @param organitzador nom de l'organitzador.
     * @return llista d'Esdeveniment organitzats per aquest organitzador.
     */
    List<Esdeveniment> findByOrganitzador(String organitzador);

}
