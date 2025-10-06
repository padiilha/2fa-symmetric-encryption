package server.service;

import model.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileService {

    private final String STORE_FILE = "files.ser";
    private final Map<String, File> files;

    public FileService() {
        this.files = loadFiles();
    }

    public void uploadFile(String name, String content, String owner) {
        var file = new File();
        file.setFileName(name);
        file.setFileContent(content);
        file.setOwner(owner);
        files.put(file.getOwner(), file);
        saveFiles();

        System.out.println("Arquivo armazenado com sucesso.");
    }

    public String downloadFile(String owner) {
        var file = files.get(owner);

        if (file.equals(null)) throw new RuntimeException();

        return file.getFileContent();
    }

    private void saveFiles() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE))) {
            oos.writeObject(files);
            System.out.println(files.size() + " arquivos foram salvos com sucesso em: " + STORE_FILE);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivos: " + e.getMessage());
        }
    }

    private Map<String, File> loadFiles() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_FILE))) {
            return (Map<String, File>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivos não encontrados.");
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao ler arquivos: " + e.getMessage());
            return new HashMap<>();
        }
    }

}
