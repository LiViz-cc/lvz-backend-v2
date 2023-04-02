package com.liviz.v2.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomStringGenerator {
    public String generatePassword(int length) {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialCharacters = "!@#$%^&*()-_=+[{]}|;:'\",<.>/?";
        String allChars = upperCaseLetters + lowerCaseLetters + digits + specialCharacters;

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }

        return password.toString();
    }

    public String generateUsername(int length) {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String allChars = upperCaseLetters + lowerCaseLetters + digits;

        StringBuilder username = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allChars.length());
            username.append(allChars.charAt(randomIndex));
        }

        return username.toString();
    }
}
