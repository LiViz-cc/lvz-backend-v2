package com.liviz.v2.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Component
public class RandomStringGenerator {

    private final Log logger = LogFactory.getLog(this.getClass());

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

    // refer to https://stackoverflow.com/questions/17947026/java-method-which-can-provide-the-same-output-as-python-method-for-hmac-sha256-i
    public String generateUsername(String email) {
        try {
            // get HMAC sha256 algorithm
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            // set secret key
            final String randomSecret = "wje9ewEWFX";
            SecretKeySpec secretKey = new SecretKeySpec(randomSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            // get HMAC sha256 hash
            byte[] hash = sha256_HMAC.doFinal(email.getBytes());
            // convert to hex string
            String check = Hex.encodeHexString(hash);
            // set username with first 10 characters of hex string
            return check.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            logger.debug("NoSuchAlgorithmException, " + e.getMessage());
        } catch (InvalidKeyException e) {
            logger.debug("InvalidKeyException, " + e.getMessage());
        }
        return null;
    }
}
