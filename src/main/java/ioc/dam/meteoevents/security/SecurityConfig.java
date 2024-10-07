package ioc.dam.meteoevents.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF (solo si no usas formularios)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/usuaris/login", "/api/usuaris/registre").permitAll() // Permitir acceso sin autenticación
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                )
                .formLogin(form -> form.disable()) // Deshabilitar el inicio de sesión por defecto
                .httpBasic(httpBasic -> httpBasic.disable()); // Deshabilitar autenticación básica

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usar BCrypt para codificar contraseñas
    }
}
