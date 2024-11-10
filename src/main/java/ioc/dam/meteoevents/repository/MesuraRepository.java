package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.Mesura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori per a l'entitat {@link Mesura}.
 * Aquest repositori extén {@link JpaRepository} i proporciona mètodes per interactuar amb la base de dades
 *
 * @author Generat amb IA (ChatGPT).
 * @prompt "Genera una classe que utilitzi Spring Data JPA per interactuar amb la Base de dades PostgreSQL"
 */
@Repository
public interface MesuraRepository extends JpaRepository<Mesura, Integer> {
    // Afegir mètodes personalitzats si cal
}
