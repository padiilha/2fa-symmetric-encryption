package model;

import java.io.Serial;
import java.io.Serializable;

public class File implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String fileName;
    private String fileContent;
    private String owner;

    public File() {}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
