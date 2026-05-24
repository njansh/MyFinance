package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;

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
        if (name == null || name.isBlank()) throw new BusinessRuleException("Name is required");
        if (email == null || !email.contains("@")) throw new BusinessRuleException("Invalid email format");
        if (password == null || password.isBlank()) throw new BusinessRuleException("Password is required");
    }


    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void updateProfile(String name, String email) {
        if (name != null) {
            if (name.isBlank()) throw new BusinessRuleException("Name cannot be blank");
            this.name = name;
        }else this.name = this.name;

        if (email != null) {
            if (!email.contains("@")) throw new BusinessRuleException("Invalid email format");
            this.email = email;
        }else this.email = this.email;
    }

    public void changePassword(String newPassword, String encodedPassword) {
        if (newPassword == null || newPassword.isBlank()) {
throw new BusinessRuleException("New password cannot be blank");
        }
        this.password = encodedPassword;
    }

}
