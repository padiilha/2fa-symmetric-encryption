import client.service.FileClientService;
import client.service.RegisterClientService;

import java.util.Scanner;

public class Application {

    private static final FileClientService fileClientService = new FileClientService();
    private static final RegisterClientService registerClientService = new RegisterClientService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Trabalho Prático 2 - 2FA Symmetric Encryption");

        System.out.println("Selecione o que deseja fazer: (1) Criar novo usuário - (2) Logar em usuário existente");
        var option = scanner.nextLine();

        System.out.println("Digite o usuário");
        var username = scanner.nextLine();

        System.out.println("Digite a senha");
        var password = scanner.nextLine();

        var userAuthenticated = false;

        switch (option) {
            case "1" -> userAuthenticated = registerClientService.registerUser(username, password);
            case "2" -> userAuthenticated = registerClientService.loginUser(username, password);
        }

        if (!userAuthenticated) throw new RuntimeException();

        System.out.println("Selecione o que deseja fazer: (1) Enviar novo arquivo criptografado - (2) Resgatar arquivo criptografado");
        option = scanner.nextLine();

        switch (option) {
            case "1" -> fileClientService.uploadFile(username);
            case "2" -> fileClientService.downloadFile(username);
        }
    }

}
