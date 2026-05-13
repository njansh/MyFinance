package com.nadson.myfinance.application.port.out;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
}