package ru.mtuci.rbpo_2024_praktika.model;

import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    public static PrivateKey loadPrivateKey() throws Exception {
        try (BufferedReader privateKeyReader = new BufferedReader(new FileReader("private.key"))) {
            String privateKeyBase64 = privateKeyReader.readLine();
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
        }
    }

    public static PublicKey loadPublicKey() throws Exception {
        try (BufferedReader publicKeyReader = new BufferedReader(new FileReader("public.key"))) {
            String publicKeyBase64 = publicKeyReader.readLine();
            byte[] decodedKey = Base64.getDecoder().decode(publicKeyBase64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        }
    }
}
