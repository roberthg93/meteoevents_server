package ioc.dam.meteoevents.security;

import ioc.dam.meteoevents.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuració de la seguretat de l'aplicació amb Spring Security.
 * Gestiona la seguretat a nivell de HTTP, incloent el filtrat de JWT, autenticació i permisos d'accés.
 *
 * @author Generat amb IA (ChatGPT)
 * @prompt "Si intento fer una petició a la meva aplicació via API REST amb HTTP em dirigeix a una pàgina de login"
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructor de la classe {@code SecurityConfig}.
     * Aquest constructor inicialitza el filtre de sol·licitud JWT utilitzat per validar els tokens de seguretat en cada petició.
     *
     * @param jwtRequestFilter filtre per validar les sol·licituds HTTP amb tokens JWT.
     * @author Generat amb IA (ChatGPT)
     * <b>Prompt original:</b>
     * "Si el login és correcte, genera i retorna un identificador de sessió implementant JWT que s'utilitzi per totes les peticions del frontend"
     *
     * <b>Correcció en el prompt:</b>
     * "Al tractar d'arrencar l'aplicació rebo error -The dependencies of some of the beans in the application context form a cycle- aplica Lazy"
     */
    @Autowired
    public SecurityConfig(@Lazy JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Defineix la cadena de seguretat per a la configuració de les peticions HTTP.
     * Deshabilita el CSRF, estableix permisos d'accés per a certs endpoints i aplica el filtre JWT.
     *
     * @param http l'objecte {@code HttpSecurity} per personalitzar la seguretat de HTTP.
     * @return una instància configurada de {@code SecurityFilterChain}.
     * @throws Exception si hi ha algun error en la configuració.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si intento fer una petició a la meva aplicació via API REST amb HTTP em dirigeix a una pàgina de login"
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/usuaris/login", "/api/usuaris/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Afegir el filtre JWT abans del filtre d'autenticació de Spring
                .formLogin(form -> form.disable()) // Deshabilitar el formulari de login per defecte
                .httpBasic(basic -> basic.disable()); // Deshabilitar l'autenticació bàsica

        return http.build();
    }

    /**
     * Bean per a l'encoder de contrasenyes que utilitza BCrypt per codificar les contrasenyes dels usuaris.
     *
     * @return una instància de {@code BCryptPasswordEncoder}.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si intento fer una petició a la meva aplicació via API REST amb HTTP em dirigeix a una pàgina de login"
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Per codificar les contrasenyes
    }

    /**
     * Bean de {@code UserDetailsService} per carregar els usuaris des de la base de dades.
     * Aquest mètode pot ser utilitzat per implementar lògica personalitzada per carregar els usuaris des de fonts com la base de dades.
     *
     * @return una instància de {@code UserDetailsService}.
     * @author Generat amb IA (ChatGPT)
     * @prompt "Si intento fer una petició a la meva aplicació via API REST amb HTTP em dirigeix a una pàgina de login"
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Implementació de UserDetailsService per carregar els usuaris des de la BD
        return username -> {
            // Aquí va la lògica per carregar l'usuari de la BD
            return null;
        };
    }

}
