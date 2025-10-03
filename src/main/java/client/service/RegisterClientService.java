package client.service;

import server.service.RegisterService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;

public class RegisterClientService {

    private RegisterService registerService;

    // PBKDF2 token generation
    private String generateAuthToken(String username, String password) {
        var salt = getSalt(username, password);
        var spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 128);

        try {
            var pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512, BCFIPS");
            var sk = pbkdf2.generateSecret(spec);
            return HexFormat.of().formatHex(sk.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getSalt(String username, String password) {
        var nonHashedSalt = username + password;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(nonHashedSalt.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(String username, String password) {
        var authToken = generateAuthToken(username, password);
        // Sends authToken to server
        var totpSecret = registerService.saveUser(username, authToken);
        System.out.println("Usuário registrado com sucesso.");
        System.out.println("TOTP Secret: " + totpSecret);
    }

}
