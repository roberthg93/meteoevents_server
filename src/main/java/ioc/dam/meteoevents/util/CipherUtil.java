package ioc.dam.meteoevents.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Classe utilitària per gestionar la xifra i desxifra de dades.
 * Aquesta implementació utilitza l'algoritme AES per protegir les dades sensibles
 * com les contrasenyes.
 *
 * @author Chat-GPT. Prompt: Construeix classe unitaria per xifrar i desxifrar utilitzant Cipher
 */
public class CipherUtil {
    private static final String ALGORITHM = "AES"; // Algoritme de xifrat
    private static final byte[] SECRET_KEY = "MeteoEventsSecrt".getBytes(); // Clau de 16 bytes (modificar per ser segura)
    private static final String ENCRYPTION_PREFIX = "ENC_"; // Prefix per identificar dades xifrades

    // Constructor privat per evitar instanciació
    private CipherUtil() {
    }

    /**
     * Xifra una cadena de text utilitzant l'algoritme AES i afegeix el prefix "ENC_".
     *
     * @param data el text pla que es vol xifrar.
     * @return el text xifrat en format Base64 amb el prefix "ENC_".
     * @throws Exception si hi ha algun error durant la xifra.
     */
    public static String encrypt(String data) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Inicialitzar el Cipher en mode ENCRYPT
        byte[] encryptedData = cipher.doFinal(data.getBytes()); // Xifrar les dades
        return ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(encryptedData); // Afegir prefix i codificar a Base64
    }

    /**
     * Desxifra una cadena de text xifrada utilitzant l'algoritme AES.
     * Només es desxifra si el text comença amb el prefix "ENC_".
     *
     * @param encryptedData el text xifrat en format Base64 amb el prefix "ENC_".
     * @return el text desxifrat en text pla.
     * @throws IllegalArgumentException si el text no conté el prefix "ENC_".
     * @throws Exception si hi ha algun error durant la desxifra.
     */
    public static String decrypt(String encryptedData) throws Exception {
        if (!encryptedData.startsWith(ENCRYPTION_PREFIX)) {
            throw new IllegalArgumentException("Les dades no estan xifrades amb el format esperat.");
        }

        String base64Data = encryptedData.substring(ENCRYPTION_PREFIX.length()); // Elimina el prefix
        if (!base64Data.matches("^[A-Za-z0-9+/=]*$")) {
            throw new IllegalArgumentException("Les dades contenen caràcters no vàlids per Base64.");
        }

        SecretKey secretKey = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        if (SECRET_KEY.length != 16) {
            throw new IllegalArgumentException("La clau secreta ha de tenir 16 bytes.");
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey); // Inicialitzar el Cipher en mode DECRYPT
        byte[] decodedData = Base64.getDecoder().decode(base64Data); // Decodificar de Base64
        return new String(cipher.doFinal(decodedData)); // Desxifrar les dades
    }
}
