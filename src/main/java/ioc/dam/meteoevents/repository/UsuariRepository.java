package ioc.dam.meteoevents.repository;

import ioc.dam.meteoevents.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    Optional<Usuari> findByNomUsuari(String nomUsuari);
}
