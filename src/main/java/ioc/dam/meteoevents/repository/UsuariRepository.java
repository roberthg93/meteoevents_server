package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats {@link Usuari}.
 * Aquest repositori extén {@link JpaRepository} i proporciona mètodes per interactuar amb la base de dades,
 * incloent operacions CRUD (Crear, Llegir, Actualitzar, Esborrar) i altres operacions personalitzades.
 *
 * @author Generat amb IA (ChatGPT).
 * @prompt "Genera una classe que utilitzi Spring Data JPA per interactuar amb la Base de dades PostgreSQL"
 */

public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    /**
     * Cerca un usuari en la base de dades pel seu nom d'usuari.
     *
     * @param nomUsuari el nom d'usuari a cercar.
     * @return un {@link Optional} que conté l'entitat {@link Usuari} si es troba, o buit si no es troba.
     * @author Generat amb IA (ChatGPT).
     */
    Optional<Usuari> findByNomUsuari(String nomUsuari);
}
