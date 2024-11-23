package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.EsdevenimentUsuari;
import ioc.dam.meteoevents.entity.MesuraEsdeveniment;
import ioc.dam.meteoevents.entity.MesuraEsdevenimentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per a l'entitat {@link MesuraEsdeveniment}.
 * Aquest repositori extén {@link JpaRepository} i proporciona mètodes per interactuar amb la base de dades
 *
 * @author Generat amb IA (ChatGPT). Prompt: "Genera una classe que utilitzi Spring Data JPA per interactuar amb la Base de dades PostgreSQL"
 */
@Repository
public interface MesuraEsdevenimentRepository extends JpaRepository<MesuraEsdeveniment, MesuraEsdevenimentId> {
    /**
     * Troba totes les associacions per a una mesura específica, basant-se en el seu id.
     *
     * @param idMesura identificador de la mesura.
     * @return llista de MesuraEsdeveniment associats a aquesta mesura.
     */
    List<MesuraEsdeveniment> findByIdMesura(Integer idMesura);

    /**
     * Troba totes les associacions per a un esdeveniment específic, basant-se en el seu id.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     * @return llista de MesuraEsdeveniment associats a aquest esdeveniment.
     */
    List<MesuraEsdeveniment> findByIdEsdeveniment(Integer idEsdeveniment);

    /**
     * Troba totes les associacions per a un esdeveniment específic i una mesura específica
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     * @param idMesura identificador de la mesura.
     * @return llista de MesuraEsdeveniment associats a aquest esdeveniment i mesura
     */
    Optional<MesuraEsdeveniment> findByIdEsdevenimentAndIdMesura(Integer idEsdeveniment, Integer idMesura);


    /**
     * Elimina totes les associacions per a una mesura específica.
     *
     * @param idMesura identificador de la mesura.
     */
    void deleteByIdMesura(Integer idMesura);

    /**
     * Elimina totes les associacions per a un esdeveniment específic.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     */
    void deleteByIdEsdeveniment(Integer idEsdeveniment);
}
