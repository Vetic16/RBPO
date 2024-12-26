package ru.mtuci.rbpo_2024_praktika.model;

import java.io.*;
import java.security.*;
import java.util.Base64;

public class KeyGeneratorUtil {

    public static void generateAndSaveKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        try (BufferedWriter privateKeyWriter = new BufferedWriter(new FileWriter("private.key"))) {
            String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            privateKeyWriter.write(privateKeyBase64);
        }

        try (BufferedWriter publicKeyWriter = new BufferedWriter(new FileWriter("public.key"))) {
            String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            publicKeyWriter.write(publicKeyBase64);
        }
    }

    public static void main(String[] args) throws Exception {
        generateAndSaveKeys();
        System.out.println("Keys generated and saved successfully.");
    }
}
