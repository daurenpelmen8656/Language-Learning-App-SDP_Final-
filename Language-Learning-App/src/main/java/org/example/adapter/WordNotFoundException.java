package org.example.adapter;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String word) {
        super("Word not found: " + word);
    }
}
