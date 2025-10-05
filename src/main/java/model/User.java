package model;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String username;
    private String scryptToken;
    private String totpSecret;

    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScryptToken() {
        return scryptToken;
    }

    public void setScryptToken(String scryptToken) {
        this.scryptToken = scryptToken;
    }

    public String getTotpSecretKey() {
        return totpSecret;
    }

    public void setTotpSecretKey(String totpSecret) {
        this.totpSecret = totpSecret;
    }

}
