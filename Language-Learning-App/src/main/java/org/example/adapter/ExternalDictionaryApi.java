package org.example.adapter;

/**
 * Simulates a third-party dictionary with a format we cannot change ("word|translation").
 */
public class ExternalDictionaryApi {
    public String getEntry(String key) {
        return switch (key.toLowerCase()) {
            case "apple" -> "apple|яблоко";
            case "window" -> "window|окно";
            case "book" -> "book|книга";
            case "language" -> "language|язык";
            case "pattern" -> "pattern|паттерн";
            case "student" -> "student|студент";
            case "water" -> "water|вода";
            case "sun" -> "sun|солнце";
            default -> throw new IllegalArgumentException("Word not found: " + key);
        };
    }
}
