package ru.mtuci.Dubovikov.utils;

import java.util.UUID;

public class ActivationCodeGenerator {

    public static String generateCode() {
        return UUID.randomUUID().toString();
    }
}
