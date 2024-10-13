package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.mapper.UsuariMapper;
import ioc.dam.meteoevents.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuariService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*public UsuariDTO registraUsuari(UsuariDTO usuariDTO, String contrasenya) {
        Usuari usuari = UsuariMapper.INSTANCE.toEntity(usuariDTO);
        usuari.setContrasenya(contrasenya);
        Usuari usuariGuardat = usuariRepository.save(usuari);
        return UsuariMapper.INSTANCE.toDTO(usuariGuardat);
    }*/

    public Usuari autenticar(String nomUsuari, String contrasenya) {
        Optional<Usuari> usuariOpt = usuariRepository.findByNomUsuari(nomUsuari);
        if (usuariOpt.isPresent() && contrasenya.equals(usuariOpt.get().getContrasenya())) {
            return usuariOpt.get();
        }
        return null;
    }
}
