package server.service;

import com.lambdaworks.crypto.SCryptUtil;
import model.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegisterService {

    private final String FILE_NAME = "resources/users.txt";

    // TODO: improve file handling to support multiple users
    private User getUserFromFile(String username) {
        User file = null;

        try {
            FileInputStream fileIn = new FileInputStream(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            file = (User) in.readObject();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (ClassNotFoundException c) {
            System.err.println("Falha na autenticação.");
        }
        return (User) file;
    }

    // Scrypt token generation
    private String generateScryptToken(String authToken) {
        var cost = 16384;
        var blockSize = 8;
        var parallelization = 1;

        return SCryptUtil.scrypt(authToken, cost, blockSize, parallelization);
    }

    // TODO: Logic to generate a TOTP secret
    private String generateTOTPSecret() {
        return "";
    }

    public String saveUser(String username, String authToken) {
        var scryptToken = generateScryptToken(authToken);
        var user = new User();

        user.setUsername(username);
        user.setScryptToken(scryptToken);

        // TODO: improve file handling to support multiple users
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(user);
            System.out.println("Registo do usuário realizado no arquivo " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Erro no registro do usuário: " + e.getMessage());
        }

        var totpSecret = generateTOTPSecret();
        // TODO: save TOTP secret into a file

        return totpSecret;
    }

    public void loginUser(String username, String authToken) {
        var user = getUserFromFile(username);
        var scryptToken = generateScryptToken(authToken);
        var storagedToken = user.getScryptToken();

        if (!scryptToken.equals(storagedToken)) System.err.println("Falha na autenticação");

        System.out.println("Usuário autenticado com sucesso");
    }

}
