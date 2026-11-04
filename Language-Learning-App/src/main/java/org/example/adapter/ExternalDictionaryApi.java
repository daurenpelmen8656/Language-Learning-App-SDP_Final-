package org.example.adapter;

/** Simulates a third-party dictionary with a format we cannot change. */
public class ExternalDictionaryApi {
    public String getEntry(String key) {
        return switch (key.toLowerCase()) {
            case "apple" -> "apple|яблоко";
            case "window" -> "window|окно";
            default -> throw new IllegalArgumentException("Word not found: " + key);
        };
    }
}
