package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.*;
import ioc.dam.meteoevents.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private MesuraRepository mesuraRepository;

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

    /**
     * Obté la llista d'Usuaris en funció de l'Id de l'esdeveniment
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment a cercar usuaris assignats.
     * @return List<Usuari>
     * @author rhospital
     */
    public List<Usuari> obtenirUsuarisPerEsdeveniment(Integer idEsdeveniment) {
        // Obtenim totes les associacions d'usuaris per a l'esdeveniment especificat
        List<EsdevenimentUsuari> associacions = esdevenimentUsuariRepository.findByIdEsdeveniment(Long.valueOf(idEsdeveniment));

        // Extraiem els ids d'usuaris de les associacions
        List<Long> idsUsuaris = associacions.stream()
                .map(EsdevenimentUsuari::getIdUsuari)
                .collect(Collectors.toList());

        // Retornem els usuaris corresponents als ids obtinguts
        return usuariRepository.findAllById(idsUsuaris);
    }

    /**
     * Obté la llista de Mesures en funció de l'Id de l'esdeveniment
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment a cercar mesures assignades.
     * @return List<Mesura>
     * @author rhospital
     */
    public List<Mesura> obtenirMesuresPerEsdeveniment(Integer idEsdeveniment) {
        // Obtenim totes les associacions de mesures per a l'esdeveniment especificat
        List<MesuraEsdeveniment> associacions = mesuraEsdevenimentRepository.findByIdEsdeveniment(idEsdeveniment);

        // Extraiem els ids de mesures de les associacions
        List<Integer> idsMesures = associacions.stream()
                .map(MesuraEsdeveniment::getIdMesura)
                .collect(Collectors.toList());

        // Retornem les mesures corresponents als ids obtinguts
        return mesuraRepository.findAllById(idsMesures);
    }

    /**
     * Afegeix un Usuari a un Esdeveniment determinat
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment a associar l'usuari
     * @param idUsuari l'identificador únic de l'usuari a afegir
     * @return booleà en funció de si s'ha afegit la Mesura o no
     * @author rhospital
     */
    public boolean afegirUsuariAEsdeveniment(Integer idEsdeveniment, Long idUsuari) {
        Optional<Esdeveniment> esdeveniment = obtenirEsdevenimentPerId(idEsdeveniment);
        Optional<Usuari> usuari = usuariRepository.findById(idUsuari);

        if (esdeveniment.isPresent() && usuari.isPresent()) {
            // Crear l'entitat que relaciona l'Esdeveniment i l'Usuari
            EsdevenimentUsuari novaRelacio = new EsdevenimentUsuari();
            novaRelacio.setIdEsdeveniment(Long.valueOf(idEsdeveniment));
            novaRelacio.setIdUsuari(idUsuari);

            // Desar la relació
            esdevenimentUsuariRepository.save(novaRelacio);
            return true;
        }
        return false;
    }

    /**
     * Afegeix una Mesura de prevenció a un Esdeveniment determinat
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment a associar la mesura
     * @param idMesura l'identificador únic de la mesura a afegir
     * @return booleà en funció de si s'ha afegit la Mesura o no
     * @author rhospital
     */
    public boolean afegirMesuraAEsdeveniment(Integer idEsdeveniment, Integer idMesura) {
        Optional<Esdeveniment> esdeveniment = obtenirEsdevenimentPerId(idEsdeveniment);
        Optional<Mesura> mesura = mesuraRepository.findById(idMesura);

        if (esdeveniment.isPresent() && mesura.isPresent()) {
            // Crear l'entitat que relaciona l'Esdeveniment i la Mesura
            MesuraEsdeveniment novaRelacio = new MesuraEsdeveniment();
            novaRelacio.setIdEsdeveniment(idEsdeveniment);
            novaRelacio.setIdMesura(idMesura);

            // Desar la relació
            mesuraEsdevenimentRepository.save(novaRelacio);
            return true;
        }
        return false;
    }

    /**
     * Elimina un Usuari assignat a un Esdeveniment determinat
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment a eliminar l'usuari
     * @param idUsuari l'identificador únic de l'usuari a eliminar
     * @return booleà en funció de si s'ha eliminat l'Usuari o no
     * @author rhospital
     */
    public boolean eliminarUsuariAssignatEsdeveniment(Integer idEsdeveniment, Long idUsuari) {
        // Verifica que l'esdeveniment i l'usuari existeixin
        Optional<Esdeveniment> esdeveniment = obtenirEsdevenimentPerId(idEsdeveniment);
        Optional<Usuari> usuari = usuariRepository.findById(idUsuari);

        if (esdeveniment.isPresent() && usuari.isPresent()) {
            // Comprova que l'usuari està assignat a l'esdeveniment
            Optional<EsdevenimentUsuari> assignacio = esdevenimentUsuariRepository
                    .findByIdEsdevenimentAndIdUsuari(idEsdeveniment, idUsuari);

            if (assignacio.isPresent()) {
                esdevenimentUsuariRepository.delete(assignacio.get());
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina una Mesura de prevenció assignada a un Esdeveniment determinat
     *
     * @param idEsdeveniment l'identificador únic de l'esdeveniment a eliminar la mesura
     * @param idMesura l'identificador únic de la mesura a eliminar
     * @return booleà en funció de si s'ha eliminat la Mesura o no
     * @author rhospital
     */
    public boolean eliminarMesuraAssignadaEsdeveniment(Integer idEsdeveniment, Integer idMesura) {
        // Verifica que l'esdeveniment i la mesura existeixin
        Optional<Esdeveniment> esdeveniment = obtenirEsdevenimentPerId(idEsdeveniment);
        Optional<Mesura> mesura = mesuraRepository.findById(idMesura);

        if (esdeveniment.isPresent() && mesura.isPresent()) {
            // Comprova que la mesura està assignada a l'esdeveniment
            Optional<MesuraEsdeveniment> assignacio = mesuraEsdevenimentRepository
                    .findByIdEsdevenimentAndIdMesura(idEsdeveniment, idMesura);

            if (assignacio.isPresent()) {
                mesuraEsdevenimentRepository.delete(assignacio.get());
                return true;
            }
        }
        return false;
    }


}
