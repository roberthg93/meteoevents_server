package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.EsdevenimentUsuari;
import ioc.dam.meteoevents.entity.EsdevenimentUsuariId;
import ioc.dam.meteoevents.entity.MesuraEsdeveniment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per gestionar les operacions de persistència per a l'entitat EsdevenimentUsuari.
 * Proporciona mètodes per accedir a les associacions entre usuaris i esdeveniments a la base de dades.
 *
 * Utilitza Spring Data JPA per facilitar la interacció amb la base de dades.
 *
 * @author rhospital
 */
@Repository
public interface EsdevenimentUsuariRepository extends JpaRepository<EsdevenimentUsuari, EsdevenimentUsuariId> {

    /**
     * Troba totes les associacions per a un usuari específic, basant-se en el seu id.
     *
     * @param idUsuari identificador de l'usuari.
     * @return llista d'EsdevenimentUsuari associats a aquest usuari.
     */
    List<EsdevenimentUsuari> findByIdUsuari(Long idUsuari);

    /**
     * Troba totes les associacions per a un esdeveniment específic, basant-se en el seu id.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     * @return llista d'EsdevenimentUsuari associats a aquest esdeveniment.
     */
    List<EsdevenimentUsuari> findByIdEsdeveniment(Long idEsdeveniment);

    /**
     * Troba totes les associacions per a un esdeveniment específic i un Usuari específic, basant-se en el seus ids.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     * @param idUsuari identificador de l'usuari.
     * @return llista de EsdevenimentUsuari associats a aquest esdeveniment i usuari
     */
    Optional<EsdevenimentUsuari> findByIdEsdevenimentAndIdUsuari(Integer idEsdeveniment, Long idUsuari);

    /**
     * Elimina totes les associacions per a un usuari específic.
     *
     * @param idUsuari identificador de l'usuari.
     */
    void deleteByIdUsuari(Long idUsuari);

    /**
     * Elimina totes les associacions per a un esdeveniment específic.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     */
    void deleteByIdEsdeveniment(Long idEsdeveniment);
}
