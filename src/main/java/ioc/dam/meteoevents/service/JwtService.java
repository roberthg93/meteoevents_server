package ioc.dam.meteoevents.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import ioc.dam.meteoevents.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtService {

    private final UsuariRepository usuariRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtService(UsuariRepository usuariRepository, JwtUtil jwtUtil) {
        this.usuariRepository = usuariRepository;
        this.jwtUtil = jwtUtil;
    }

    public Optional<Usuari> getUserFromToken(String token) {
        try {
            String nomUsuari = jwtUtil.extreureNomUsuari(token);
            return usuariRepository.findByNomUsuari(nomUsuari);
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirat");
            return Optional.empty();
        } catch (JwtException e) {
            System.out.println("Token invalid");
            return Optional.empty();
        }
    }

    public boolean validarToken(String token, String nomUsuari) {
        return jwtUtil.validarToken(token, nomUsuari);
    }
}
