package com.example.hospital_system.Models;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty role;
    private final BooleanProperty loggedIn;

    public User(int id, String username, String password,String role, Boolean loggedIn) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.role = new SimpleStringProperty(role);
        this.loggedIn = new SimpleBooleanProperty(loggedIn);
    }

    public IntegerProperty idProperty(){
        return id;
    }

    public int getId(){
        return id.get();
    }

    public void setId(int id){
        this.id.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username){
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }



    public String getPassword() {
        return password.get();
    }
    public void setPassword(String password){
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }



    public String getRole() {
        return role.get();
    }
    public void setRole(String role){
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public BooleanProperty loggedInProperty(){
        return  loggedIn;
    }

    public Boolean getLoggedIn(){
        return loggedIn.get();
    }

    public void setLoggedIn(Boolean loggedIn){
        this.loggedIn.set(loggedIn);
    }



    public void login() {
        setLoggedIn(true);
        System.out.println(getUsername() + " logged in successfully!");
    }

    public void logout() {
        setLoggedIn(false);
        System.out.println(getUsername() + " logged in successfully!");
    }

}
