package ioc.dam.meteoevents.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "usuaris")
@Getter
@Setter

public class Usuari {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom_c;

    @Column(nullable = false)
    private String funcional_id;

    @Column(name = "nom_usuari", nullable = false, unique = true)
    private String nomUsuari;

    @Column(nullable = false)
    private String contrasenya;

    @Column
    private String ultima_connexio;

}
