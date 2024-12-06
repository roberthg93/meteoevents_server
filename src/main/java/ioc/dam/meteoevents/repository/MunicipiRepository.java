package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.Municipi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositori per a l'entitat {@link Municipi}.
 * Aquest repositori extén {@link JpaRepository} i proporciona mètodes per interactuar amb la base de dades
 *
 * @author Generat amb IA (ChatGPT).
 * @prompt "Genera una classe que utilitzi Spring Data JPA per interactuar amb la Base de dades PostgreSQL"
 */
@Repository
public interface MunicipiRepository extends JpaRepository<Municipi, Integer> {
    /**
     * Troba el codi d'un municipi donat el seu nom.
     *
     * @param municipi el nom del municipi.
     * @return el codi del municipi si es troba, o null en cas contrari.
     */
    @Query("SELECT m.codi FROM Municipi m WHERE m.municipi = :municipi")
    String findCodiByMunicipi(@Param("municipi") String municipi);
}
