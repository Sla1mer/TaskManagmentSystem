package com.example.CRMAuthBackend.utils;

import java.security.SecureRandom;

public class RandomCodeGenerator {
    public static String generateRandomCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Длина кода должна быть положительным числом.");
        }

        // Создаем генератор криптографически стойких случайных чисел
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomDigit = secureRandom.nextInt(10); // Генерируем случайную цифру от 0 до 9
            code.append(randomDigit);
        }

        return code.toString();
    }
}
