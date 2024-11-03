package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servei creat especialment perquè quan l'aplicació aplica el filtre JwtRequestFilter
 * pugui resoldre positivament el mètode userDetailsService.loadUserByUsername(nomUsuari);
 *
 * @author rhospital
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuariRepository usuariRepository;

    public CustomUserDetailsService(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

    /**
     * Carrega un usuari pel seu nom d'usuari.
     * @param username El nom d'usuari.
     * @return Detalls de l'usuari (UserDetails).
     * @throws UsernameNotFoundException Si l'usuari no existeix.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Carrega l'usuari des de la base de dades
        Usuari usuari = usuariRepository.findByNomUsuari(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat amb nom d'usuari: " + username));

        // Retorna un UserDetails a partir de l'objecte Usuari
        return new org.springframework.security.core.userdetails.User(
                usuari.getNomUsuari(),
                usuari.getContrasenya(),
                usuari.getAuthorities()
        );
    }
}
