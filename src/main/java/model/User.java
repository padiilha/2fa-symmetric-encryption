package model;

public class User {

    private String username;
    private String scryptToken;

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

}
