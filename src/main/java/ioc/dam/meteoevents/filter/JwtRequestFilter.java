package ioc.dam.meteoevents.filter;

import io.jsonwebtoken.ExpiredJwtException;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.JwtService;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Filtre per processar les peticions HTTP i validar els tokens JWT.
 * Aquest filtre extreu el token de l'encapçalament Authorization i el valida.
 * Si el token és vàlid, l'usuari associat s'autentica en el context de seguretat de Spring.
 *
 * @author Generat amb IA (ChatGPT)
 * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    private TokenManager tokenManager;

    /**
     * Constructor del filtre JwtRequestFilter.
     *
     * @param jwtService Servei per gestionar tokens JWT.
     * @param userDetailsService Servei per carregar detalls d'usuari.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    @Autowired
    public JwtRequestFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Realitza el filtratge de les peticions HTTP.
     * Extreu el token JWT de l'encapçalament Authorization, valida el token
     * i autentica l'usuari si el token és vàlid.
     *
     * @param request La petició HTTP.
     * @param response La resposta HTTP.
     * @param chain La cadena de filtres a continuar.
     * @throws ServletException Si es produeix un error al processar la petició.
     * @throws IOException Si es produeix un error d'entrada/sortida.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        String nomUsuari = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                Optional<Usuari> usuariOptional = jwtService.getUserFromToken(jwt);
                if (usuariOptional.isPresent()) {
                    Usuari usuari = usuariOptional.get();
                    nomUsuari = usuari.getNomUsuari();

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(nomUsuari);

                        if (userDetails != null && tokenManager.isTokenActive(jwt) && jwtService.validarToken(jwt, userDetails.getUsername())) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                }
            } catch (ExpiredJwtException e) {
                System.out.println("JWT expirat");
            } catch (Exception e) {
                System.out.println("Error al procesar el JWT: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

}
