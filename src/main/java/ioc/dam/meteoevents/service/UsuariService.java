package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Classe de servei per a la gestió de les operacions relacionades amb els usuaris.
 * Aquesta classe proporciona funcionalitats per a l'autenticació dels usuaris.
 *
 * @author rhospital
 */

@Service
public class UsuariService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Autentica un usuari basat en el seu nom d'usuari i contrasenya.
     *
     * Aquest mètode cerca un usuari a la base de dades pel seu nom d'usuari. Si es troba,
     * compara la contrasenya proporcionada amb la contrasenya de l'usuari desat. Si coincideixen,
     * retorna l'entitat {@link Usuari}. En cas contrari, retorna {@code null}.
     *
     * @param nomUsuari el nom d'usuari utilitzat per a la identificació.
     * @param contrasenya la contrasenya proporcionada per a l'autenticació.
     * @return l'entitat {@link Usuari} si l'autenticació és correcta; {@code null} si és incorrecta.
     * @author rhospital
     */
    public Usuari autenticar(String nomUsuari, String contrasenya) {
        Optional<Usuari> usuariOpt = usuariRepository.findByNomUsuari(nomUsuari);
        if (usuariOpt.isPresent() && contrasenya.equals(usuariOpt.get().getContrasenya())) {
            return usuariOpt.get();
        }
        return null;
    }

    /**
     * Retorna una llista de tots els usuaris.
     *
     * @return una llista de totes les entitats {@link Usuari} guardades a la base de dades.
     * @author rhospital
     */
    public List<Usuari> llistarUsuaris() {
        return usuariRepository.findAll();
    }

    /**
     * Cerca un usuari per identificador.
     *
     * @param id l'identificador únic de l'usuari.
     * @return un {@link Optional} que conté l'entitat {@link Usuari} si es troba, o buit si no es troba.
     * @author rhospital
     */
    public Optional<Usuari> obtenirUsuariPerId(Long id) {
        return usuariRepository.findById(id);
    }

    /**
     * Afegeix un nou usuari a la base de dades.
     *
     * @param usuari l'entitat {@link Usuari} amb les dades de l'usuari a afegir.
     * @return l'entitat {@link Usuari} afegida amb el seu identificador generat.
     * @author rhospital
     */
    public Usuari afegirUsuari(Usuari usuari) {
        return usuariRepository.save(usuari);
    }

    /**
     * Modifica les dades d'un usuari existent.
     * Si l'usuari es troba, les dades es modifiquen i es guarda; si no es troba, es crea un nou usuari amb les dades donades.
     *
     * @param id l'identificador únic de l'usuari a modificar.
     * @param usuariDetalls l'entitat {@link Usuari} amb les noves dades a actualitzar.
     * @return l'entitat {@link Usuari} actualitzada.
     * @author rhospital
     */
    public Usuari modificarUsuari(Long id, Usuari usuariDetalls) {
        return usuariRepository.findById(id).map(usuari -> {
            usuari.setNom_c(usuariDetalls.getNom_c());
            usuari.setFuncional_id(usuariDetalls.getFuncional_id());
            usuari.setNomUsuari(usuariDetalls.getNomUsuari());
            usuari.setContrasenya(usuariDetalls.getContrasenya());
            usuari.setUltima_connexio(usuariDetalls.getUltima_connexio());
            usuari.setData_naixement(usuariDetalls.getData_naixement());
            usuari.setSexe(usuariDetalls.getSexe());
            usuari.setPoblacio(usuariDetalls.getPoblacio());
            usuari.setEmail(usuariDetalls.getEmail());
            usuari.setTelefon(usuariDetalls.getTelefon());
            usuari.setDescripcio(usuariDetalls.getDescripcio());
            return usuariRepository.save(usuari);
        }).orElseGet(() -> {
            usuariDetalls.setId(id);
            return usuariRepository.save(usuariDetalls);
        });
    }

    /**
     * Elimina un usuari existent de la base de dades.
     *
     * @param id l'identificador únic de l'usuari a eliminar.
     * @return booleà en funció de si s'ha eliminat l'usuari satisfactòriament o no
     * @author rhospital
     */
    public boolean  eliminarUsuari(Long id) {
        if (usuariRepository.existsById(id)) {
            usuariRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
