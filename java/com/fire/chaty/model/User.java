package com.fire.chaty.model;

public class User {

   private String email;
   private String login;
   private String id;
   private int avatarMockUpResourse;

    public User(){

    }

    public User(String email, String login, String id, int avatarMockUpResourse) {
        this.email = email;
        this.login = login;
        this.id = id;
        this.avatarMockUpResourse = avatarMockUpResourse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAvatarMockUpResourse() {
        return avatarMockUpResourse;
    }

    public void setAvatarMockUpResourse(int avatarMockUpResourse) {
        this.avatarMockUpResourse = avatarMockUpResourse;
    }
}
