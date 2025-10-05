package client.service;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import server.service.RegisterService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RegisterClientService {

    private static final RegisterService registerService = new RegisterService();

    public RegisterClientService() {
        Security.addProvider(new BouncyCastleFipsProvider());
        if (Security.getProvider("BCFIPS") == null) {
            System.out.println("Bouncy Castle provider não disponível");
        }
    }

    public boolean registerUser(String username, String password) {
        var authToken = generateAuthToken(username, password);
        // Sends authToken to server
        registerService.registerUser(username, authToken);
        return true;
    }

    public boolean loginUser(String username, String password) {
        var authToken = generateAuthToken(username, password);
        // Sends authToken to server
        registerService.loginUser(username, authToken);
        return true;
    }

    // PBKDF2 token generation
    private String generateAuthToken(String username, String password) {
        var salt = getSalt(username, password);
        var spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 128);

        try {
            var pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS");
            var sk = pbkdf2.generateSecret(spec);
            return HexFormat.of().formatHex(sk.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getSalt(String username, String password) {
        var nonHashedSalt = username + password;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(nonHashedSalt.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
