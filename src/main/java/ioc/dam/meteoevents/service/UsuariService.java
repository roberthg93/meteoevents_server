package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.mapper.UsuariMapper;
import ioc.dam.meteoevents.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuariService {

    @Autowired
    private UsuariRepository usuariRepository;

    /*public UsuariDTO registraUsuari(UsuariDTO usuariDTO, String contrasenya) {
        Usuari usuari = UsuariMapper.INSTANCE.toEntity(usuariDTO);
        usuari.setContrasenya(contrasenya);
        Usuari usuariGuardat = usuariRepository.save(usuari);
        return UsuariMapper.INSTANCE.toDTO(usuariGuardat);
    }*/

    public boolean autenticar(String nomUsuari, String contrasenya) {
        Optional<Usuari> usuariOpt = usuariRepository.findByNomUsuari(nomUsuari);
        if (usuariOpt.isPresent()) {
            Usuari usuari = usuariOpt.get();
            return contrasenya.equals(usuari.getContrasenya());
        }
        return false;
    }
}
