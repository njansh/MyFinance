package com.nadson.myfinance.domain.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;

    public User(UUID id, String name, String email, String password) {
        this.id = (id == null) ? UUID.randomUUID() : id;
        this.name = name;
        this.email = email;
        this.password = password;
        validate();
    }

    private void validate() {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
    }


    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }
}
