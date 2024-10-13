package ioc.dam.meteoevents.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private String SECRET_KEY = "meva_clau";

    public String generarToken(String nomUsuari) {
        Map<String, Object> claims = new HashMap<>();
        return crearToken(claims, nomUsuari);
    }

    private String crearToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Boolean validarToken(String token, String nomUsuari) {
        final String tokenNomUsuari = extreureNomUsuari(token);
        return (tokenNomUsuari.equals(nomUsuari) && !isTokenExpired(token));
    }

    public String extreureNomUsuari(String token) {
        return extreureClaim(token, Claims::getSubject);
    }

    public Date extreureExpiracio(String token) {
        return extreureClaim(token, Claims::getExpiration);
    }

    public <T> T extreureClaim(String token, java.util.function.Function<Claims, T> claimsResoldre) {
        final Claims claims = extreureTotsClaims(token);
        return claimsResoldre.apply(claims);
    }

    private Claims extreureTotsClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extreureExpiracio(token).before(new Date());
    }
}