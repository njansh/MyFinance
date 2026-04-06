package com.nadson.myfinance.domain.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String email;

    public User(UUID id, String name, String email) {
        this.id = (id == null) ? UUID.randomUUID() : id;
        this.name = name;
        this.email = email;
        validate();
    }

    private void validate() {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
    }


    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}