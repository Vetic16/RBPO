package ru.mtuci.rbpo_2024_praktika.utils;

import java.util.UUID;

public class ActivationCodeGenerator {

    public static String generateCode() {
        return UUID.randomUUID().toString();
    }
}
