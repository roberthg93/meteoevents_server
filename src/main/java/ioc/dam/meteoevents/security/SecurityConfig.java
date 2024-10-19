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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(@Lazy JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

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
                .httpBasic(basic -> basic.disable()); // Deshabilitar la autenticació bàsica

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Per codificar les contrasenyes
    }

    // Bean de UserDetailsService, per si és necessari
    @Bean
    public UserDetailsService userDetailsService() {
        // Implementació de UserDetailsService per carregar els usuaris des de la BD
        return username -> {
            // Aquí va la lògica per carregar l'usuari de la BD
            return null;
        };
    }

}
