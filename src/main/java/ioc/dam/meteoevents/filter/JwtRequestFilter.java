package ioc.dam.meteoevents.filter;

import io.jsonwebtoken.ExpiredJwtException;
import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.service.CustomUserDetailsService;
import ioc.dam.meteoevents.service.JwtService;
import ioc.dam.meteoevents.util.CipherUtil;
import ioc.dam.meteoevents.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TokenManager tokenManager;

    /**
     * Constructor del filtre JwtRequestFilter.
     *
     * @param jwtService Servei per gestionar tokens JWT.
     * @param customUserDetailsService Servei per carregar detalls d'usuari.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     */
    @Autowired
    public JwtRequestFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
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

        String requestURI = request.getRequestURI();

        // Excloem el login de ser filtrat per JWT
        if (requestURI.equals("/api/usuaris/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String nomUsuari = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedJwt = authorizationHeader.substring(7);

            try {
                // Desxifrem token
                jwt = CipherUtil.decrypt(encryptedJwt);
                System.out.println("Token filter desencriptat: " + jwt);

                // Obtenim l'usuari a partir del token
                Optional<Usuari> usuariOptional = jwtService.getUserFromToken(jwt);
                if (usuariOptional.isPresent()) {
                    Usuari usuari = usuariOptional.get();
                    nomUsuari = usuari.getNomUsuari();

                    // Verifiquem que l'usuari no estigui ja autenticat en el context de seguretat
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(nomUsuari);

                        // Validem que el token sigui vàlid i actiu
                        if (userDetails != null && tokenManager.isTokenActive(jwt) && jwtService.validarToken(jwt, userDetails.getUsername())) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            // Token inactiu o no vàlid
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                    }
                } else {
                    // Usuari no trobat o token invàlid
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (ExpiredJwtException e) {
                System.out.println("JWT expirat");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Token expirat
                return;
            } catch (Exception e) {
                System.out.println("Error al procesar el JWT: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Error genèric en el token
                return;
            }
        } else {
            // Cap token proporcionat a la petició
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        chain.doFilter(request, response);
    }

}
