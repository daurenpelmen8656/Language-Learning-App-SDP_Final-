package org.example.facade;

public record WordView(String text, String translation, String kazakhTranslation, String level) {
    public WordView(String text, String translation, String level) {
        this(text, translation, translation, level);
    }
}
