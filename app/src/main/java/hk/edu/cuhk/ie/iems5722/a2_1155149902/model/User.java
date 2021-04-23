package hk.edu.cuhk.ie.iems5722.a2_1155149902.model;

import android.graphics.drawable.Drawable;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.util.ViewUtil;

public class User {
    public int id;
    public String username;
    public String password;
    public Drawable avatar;

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
    public User(int id, String username, Drawable avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }
    public String getName() {
        return username;
    }

    public Drawable getAvatar() {
        return avatar;
    }
    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = ViewUtil.StringToDrawable(avatar);
    }
}
