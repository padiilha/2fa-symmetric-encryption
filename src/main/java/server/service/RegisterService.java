package server.service;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import model.User;
import org.apache.commons.codec.binary.Base32;
import org.bouncycastle.crypto.KDFCalculator;
import org.bouncycastle.crypto.fips.Scrypt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Scanner;

import static com.google.zxing.BarcodeFormat.QR_CODE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.bouncycastle.util.Strings.toUTF8ByteArray;

public class RegisterService {

    private final String USERS_FILE = "users.ser";
    private final String QRCODE_FILE = "matrixUrl.png";
    private final Map<String, User> users;

    public RegisterService() {
        this.users = loadUsers();
        System.out.println("Dados de usuários carregado. " + users.size() + " foram encontrados.");
    }

    public void registerUser(String username, String authToken) {
        if (users.containsKey(username)) {
            System.err.println("Usuário já cadastrado.");
            throw new RuntimeException();
        }

        var scryptToken = generateScryptToken(username, authToken);
        var totpSecretKey = generateSecretKey();
        var user = new User();

        user.setUsername(username);
        user.setScryptToken(scryptToken);
        user.setTotpSecretKey(totpSecretKey);
        users.put(user.getUsername(), user);
        saveUsers();

        System.out.println("Usuário registrado com sucesso.");
        System.out.println("Google Auth URL: " + getGoogleAuthenticatorBarCode(user.getUsername(), user.getTotpSecretKey()));
    }

    public void loginUser(String username, String authToken) {
        var user = users.get(username);

        if (user.equals(null)) throw new RuntimeException();

        var scryptToken = generateScryptToken(username, authToken);
        var storagedToken = user.getScryptToken();

        if (!scryptToken.equals(storagedToken)) throw new RuntimeException();

        System.out.println("Insira o código de autenticação.");
        var scanner = new Scanner(System.in);
        var code = scanner.nextLine();

        if (!code.equals(getTotpCode(user.getTotpSecretKey()))) throw new RuntimeException();

        System.out.println("Usuário autenticado com sucesso");
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
            System.out.println(users.size() + " usuários foram salvos com sucesso em: " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de usuários: " + e.getMessage());
        }
    }

    private Map<String, User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de dados de usuários não encontrado. Criando novo arquivo...");
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao ler dados de usuários: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Scrypt token generation
    private String generateScryptToken(String username, String authToken) {
        var salt = getSalt(username, authToken);
        var cost = 16384;
        var blockSize = 8;
        var parallelization = 1;

        KDFCalculator<Scrypt.Parameters> calculator = new Scrypt.KDFFactory()
                .createKDFCalculator(Scrypt.ALGORITHM.using(salt, cost, blockSize, parallelization, toUTF8ByteArray(authToken)));

        byte[] output = new byte[32];
        calculator.generateBytes(output);
        return HexFormat.of().formatHex(output);
    }

    private byte[] getSalt(String username, String authToken) {
        var nonHashedSalt = username + authToken;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(nonHashedSalt.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 2-Factor Authentication
    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    private String getTotpCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = HexFormat.of().formatHex(bytes);
        return TOTP.getOTP(hexKey);
    }

    private String getGoogleAuthenticatorBarCode(String account, String secretKey) {
        var issuer = "ine5680";
        System.out.println("Secret Key:" + secretKey);

        var barCode = "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, UTF_8).replace("+", "%20")
                + "?secret=" + URLEncoder.encode(secretKey, UTF_8).replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, UTF_8).replace("+", "%20");

        createQrCode(barCode);
        return barCode;
    }

    private void createQrCode(String barCodeData) {
        var width = 246;
        var height = 246;

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, QR_CODE, width, height);
            FileOutputStream out = new FileOutputStream(QRCODE_FILE);
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR code para ativação da 2FA criado no arquivo: " + QRCODE_FILE);
    }

}
