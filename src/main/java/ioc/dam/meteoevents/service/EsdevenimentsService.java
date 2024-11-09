package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.repository.EsdevenimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EsdevenimentsService {
    @Autowired
    private EsdevenimentRepository esdevenimentRepository;

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
}
