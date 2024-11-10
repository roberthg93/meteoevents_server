package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Mesura;
import ioc.dam.meteoevents.repository.MesuraEsdevenimentRepository;
import ioc.dam.meteoevents.repository.MesuraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Classe de servei per a la gestió de les operacions relacionades amb les mesures.
 *
 * @author rhospital
 */
@Service
public class MesuraService {
    @Autowired
    private MesuraRepository mesuraRepository;

    @Autowired
    private MesuraEsdevenimentRepository mesuraEsdevenimentRepository;

    /**
     * Retorna una llista de totes les mesures.
     *
     * @return una llista de totes les entitats {@link Mesura} guardades a la base de dades.
     * @author rhospital
     */
    public List<Mesura> llistarMesures() {
        return mesuraRepository.findAll();
    }

    /**
     * Cerca una mesura per identificador.
     *
     * @param id l'identificador únic de la mesura.
     * @return un {@link Optional} que conté l'entitat {@link Mesura} si es troba, o buit si no es troba.
     * @author rhospital
     */
    public Optional<Mesura> obtenirMesuraPerId(Integer id) {
        return mesuraRepository.findById(id);
    }

    /**
     * Afegeix una nova mesura a la base de dades.
     *
     * @param mesura l'entitat {@link Mesura} amb les dades de la mesura a afegir.
     * @return l'entitat {@link Mesura} afegida amb el seu identificador generat.
     * @author rhospital
     */
    public Mesura afegirMesura(Mesura mesura) {
        return mesuraRepository.save(mesura);
    }

    /**
     * Modifica les dades d'una mesura existent.
     * Si la mesura es troba, les dades es modifiquen i es guarda; si no es troba, es crea una nova mesura amb les dades donades.
     *
     * @param id l'identificador únic de la mesura a modificar.
     * @param mesuraDetalls l'entitat {@link Mesura} amb les noves dades a actualitzar.
     * @return l'entitat {@link Mesura} actualitzada.
     * @author rhospital
     */
    public Mesura modificarMesura(Integer id, Mesura mesuraDetalls) {
        return mesuraRepository.findById(id).map(mesura -> {
            mesura.setCondicio(mesuraDetalls.getCondicio());
            mesura.setValor(mesuraDetalls.getValor());
            mesura.setValorUm(mesuraDetalls.getValorUm());
            mesura.setAccio(mesuraDetalls.getAccio());
            return mesuraRepository.save(mesura);
        }).orElseGet(() -> {
            mesuraDetalls.setId(id);
            return mesuraRepository.save(mesuraDetalls);
        });
    }

    /**
     * Elimina una mesura existent de la base de dades.
     *
     * L'anotació @Transactional és necessària per assegurar-se que les operacions de persistència com delete,
     * que n'hi ha dues, es realitzin dins d'una transacció
     *
     * @param id l'identificador únic de la mesura a eliminar.
     * @return booleà en funció de si s'ha eliminat la mesura satisfactòriament o no
     * @author rhospital
     */
    @Transactional
    public boolean  eliminarMesura(Integer id) {
        if (mesuraRepository.existsById(id)) {
            // Eliminar primer les associacions de la mesura de seguretat amb l'esdeveniment
            mesuraEsdevenimentRepository.deleteByIdMesura(id);
            // Eliminar mesura
            mesuraRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
