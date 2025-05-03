package ru.mtuci.rbpo_2024_praktika;

import ru.mtuci.rbpo_2024_praktika.model.KeyGeneratorUtil;

public class Main {
    public static void main(String[] args) {
        try {
            // Генерация и сохранение ключей
            KeyGeneratorUtil.generateAndSaveKeys();
            System.out.println("Ключи успешно сгенерированы и сохранены.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
