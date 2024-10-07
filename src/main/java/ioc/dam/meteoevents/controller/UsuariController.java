package ioc.dam.meteoevents.controller;

import ioc.dam.meteoevents.dto.UsuariDTO;
import ioc.dam.meteoevents.service.UsuariService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    @Autowired
    private UsuariService usuariService;

   /* @PostMapping("/registre")
    public ResponseEntity<UsuariDTO> registraUsuari(@RequestBody UsuariDTO usuariDTO, @RequestParam String contrasenya) {
        UsuariDTO usuariRegistrat = usuariService.registraUsuari(usuariDTO, contrasenya);
        return new ResponseEntity<>(usuariRegistrat, HttpStatus.CREATED);
    }*/

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuari(@RequestParam String nomUsuari, @RequestParam String contrasenya) {
        boolean autenticat = usuariService.autenticar(nomUsuari, contrasenya);
        if (autenticat) {
            return ResponseEntity.ok("Login correcte");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credencials invàlides");
        }
    }
}
