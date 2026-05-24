package com.nadson.myfinance.application.port.in;

public interface ChangePasswordPort {
    void execute(String oldPassword, String newPassword);
}