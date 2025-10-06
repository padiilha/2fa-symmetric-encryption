package client.service;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import server.service.FileService;

import javax.crypto.Cipher;
import java.security.Security;
import java.util.Scanner;

public class FileClientService {

    private final FileService fileService = new FileService();
    private final Cipher cipher;
    private final Scanner scanner = new Scanner(System.in);

    public FileClientService() {
        Security.addProvider(new BouncyCastleFipsProvider());
        if (Security.getProvider("BCFIPS") == null) {
            System.out.println("Bouncy Castle provider não disponível");
        }

        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BCFIPS");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadFile(String owner) {
        System.out.println("Insira o nome do arquivo");
        var fileName = scanner.nextLine();

        System.out.println("Insira o conteúdo do arquivo (criptografado)");
        var fileContent = scanner.nextLine();

        fileService.uploadFile(fileName, fileContent, owner);
    }

    public void downloadFile(String owner) {
        var content = fileService.downloadFile(owner);
        System.out.print("Conteúdo do arquivo: " + content);
    }

    // TODO: implement encryption to file content
    private void encryptFileContent(String content, String secretKey) {}

    // TODO: implement decryption to file content
    private void decryptFileContent(String content, String secretKey) {}

}
