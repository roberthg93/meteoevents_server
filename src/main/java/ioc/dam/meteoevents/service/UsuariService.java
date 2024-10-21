package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
