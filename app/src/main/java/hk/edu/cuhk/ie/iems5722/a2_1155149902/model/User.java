package hk.edu.cuhk.ie.iems5722.a2_1155149902.model;

public class User {
    public int id;
    public String username;
    public String password;

    public User() {
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }
}
