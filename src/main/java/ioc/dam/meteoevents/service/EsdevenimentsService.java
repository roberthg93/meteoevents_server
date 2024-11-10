package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.repository.EsdevenimentRepository;
import ioc.dam.meteoevents.repository.EsdevenimentUsuariRepository;
import ioc.dam.meteoevents.repository.MesuraEsdevenimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Classe de servei per a la gestió de les operacions relacionades amb els esdeveniments.
 *
 * @author rhospital
 */
@Service
public class EsdevenimentsService {
    @Autowired
    private EsdevenimentRepository esdevenimentRepository;

    @Autowired
    private EsdevenimentUsuariRepository esdevenimentUsuariRepository;

    @Autowired
    private MesuraEsdevenimentRepository mesuraEsdevenimentRepository;

    /**
     * Retorna una llista de tots els esdeveniments.
     *
     * @return una llista de totes les entitats {@link Esdeveniment} guardades a la base de dades.
     * @author rhospital
     */
    public List<Esdeveniment> llistarEsdeveniments() {
        return esdevenimentRepository.findAll();
    }

    /**
     * Cerca un esdeveniment per identificador.
     *
     * @param id l'identificador únic de l'esdeveniment.
     * @return un {@link Optional} que conté l'entitat {@link Esdeveniment} si es troba, o buit si no es troba.
     * @author rhospital
     */
    public Optional<Esdeveniment> obtenirEsdevenimentPerId(Integer id) {
        return esdevenimentRepository.findById(id);
    }

    /**
     * Afegeix un nou esdeveniment a la base de dades.
     *
     * @param esdeveniment l'entitat {@link Esdeveniment} amb les dades de l'esdeveniment a afegir.
     * @return l'entitat {@link Esdeveniment} afegida amb el seu identificador generat.
     * @author rhospital
     */
    public Esdeveniment afegirEsdeveniment(Esdeveniment esdeveniment) {
        return esdevenimentRepository.save(esdeveniment);
    }

    /**
     * Modifica les dades d'un esdeveniment existent.
     * Si l'esdeveniment es troba, les dades es modifiquen i es guarda; si no es troba, es crea un nou esdeveniment amb les dades donades.
     *
     * @param id l'identificador únic de l'esdeveniment a modificar.
     * @param esdevenimentDetalls l'entitat {@link Esdeveniment} amb les noves dades a actualitzar.
     * @return l'entitat {@link Esdeveniment} actualitzada.
     * @author rhospital
     */
    public Esdeveniment modificarEsdeveniment(Integer id, Esdeveniment esdevenimentDetalls) {
        return esdevenimentRepository.findById(id).map(esdeveniment -> {
            esdeveniment.setNom(esdevenimentDetalls.getNom());
            esdeveniment.setDescripcio(esdevenimentDetalls.getDescripcio());
            esdeveniment.setOrganitzador(esdevenimentDetalls.getOrganitzador());
            esdeveniment.setDireccio(esdevenimentDetalls.getDireccio());
            esdeveniment.setCodiPostal(esdevenimentDetalls.getCodiPostal());
            esdeveniment.setPoblacio(esdevenimentDetalls.getPoblacio());
            esdeveniment.setAforament(esdevenimentDetalls.getAforament());
            esdeveniment.setHorari(esdevenimentDetalls.getHorari());
            return esdevenimentRepository.save(esdeveniment);
        }).orElseGet(() -> {
            esdevenimentDetalls.setId(id);
            return esdevenimentRepository.save(esdevenimentDetalls);
        });
    }

    /**
     * Elimina un esdeveniment existent de la base de dades.
     *
     * L'anotació @Transactional és necessària per assegurar-se que les operacions de persistència com delete,
     * que n'hi ha dues, es realitzin dins d'una transacció
     *
     * @param id l'identificador únic de l'esdeveniment a eliminar.
     * @return booleà en funció de si s'ha eliminat l'esdeveniment satisfactòriament o no
     * @author rhospital
     */
    @Transactional
    public boolean  eliminarEsdeveniment(Integer id) {
        if (esdevenimentRepository.existsById(id)) {
            // Eliminar primer les associacions de l'esdeveniment amb els usuaris
            esdevenimentUsuariRepository.deleteByIdEsdeveniment(Long.valueOf(id));
            // Eliminar també tot seguit les associacions de l'esdeveniment amb les mesures de seguretat
            mesuraEsdevenimentRepository.deleteByIdEsdeveniment(id);
            // Eliminar esdeveniment
            esdevenimentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
