package ioc.dam.meteoevents.service;

import ioc.dam.meteoevents.entity.Usuari;
import ioc.dam.meteoevents.repository.UsuariRepository;
import ioc.dam.meteoevents.util.CipherUtil;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

/**
 * Servei per gestionar la migració de contrasenyes antigues a format xifrat.
 */
@Service
public class PwdEncMigracioService {
    // Encriptem totes les contasenyes amb aquest prefix per detectar si, al iniciar el programa estan encriptades o no
    private static final String ENCRYPTED_PREFIX = "ENC_";

    private final UsuariRepository usuariRepository;

    public PwdEncMigracioService(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

    /**
     * Actualitza totes les contrasenyes desades en text pla a un format xifrat.
     * Aquest mètode recorre tots els usuaris de la base de dades i xifra les seves contrasenyes si encara no ho estan.
     *
     * @throws RuntimeException si hi ha algun error durant el procés de xifra.
     */
    public void migratePasswordsToEncryptedFormat() {
        // Obtenim tots els usuaris de la base de dades
        List<Usuari> usuaris = usuariRepository.findAll();

        for (Usuari usuari : usuaris) {
            String contrasenya = usuari.getContrasenya();
            try {
                // Comprovem si la contrasenya ja està xifrada (comprovem si té el prefix)
                if (!isEncrypted(contrasenya)) {
                    // Xifrem la contrasenya i la desem
                    String encryptedPassword = encryptPassword(contrasenya);
                    usuari.setContrasenya(encryptedPassword);
                    usuariRepository.save(usuari); // Actualitza l'usuari a la base de dades
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en migrar la contrasenya per a l'usuari: " + usuari.getNomUsuari(), e);
            }
        }
    }

    /**
     * Comprova si una contrasenya està xifrada mirant si conté el prefix "ENC_".
     *
     * @param contrasenya la contrasenya a verificar.
     * @return true si la contrasenya està xifrada, false en cas contrari.
     */
    private boolean isEncrypted(String contrasenya) {
        return contrasenya != null && contrasenya.startsWith(ENCRYPTED_PREFIX);
    }

    /**
     * Xifra una contrasenya en text pla i afegeix el prefix "ENC_".
     *
     * @param plainPassword la contrasenya en text pla.
     * @return la contrasenya xifrada amb prefix.
     * @throws Exception si es produeix un error durant el procés de xifrat.
     */
    public String encryptPassword(String plainPassword) throws Exception {
        String encrypted = CipherUtil.encrypt(plainPassword);
        return encrypted;
    }

    /**
     * Desxifra una contrasenya xifrada amb el prefix "ENC_".
     *
     * @param encryptedPassword la contrasenya xifrada amb el prefix.
     * @return la contrasenya desxifrada en text pla.
     * @throws Exception si es produeix un error durant el procés de desxifrat.
     */
    public String decryptPassword(String encryptedPassword) throws Exception {
        if (isEncrypted(encryptedPassword)) {
            String withoutPrefix = encryptedPassword.substring(ENCRYPTED_PREFIX.length());
            return CipherUtil.decrypt(withoutPrefix);
        }
        throw new IllegalArgumentException("La contrasenya proporcionada no està xifrada correctament.");
    }
}
