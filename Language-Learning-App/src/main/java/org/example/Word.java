package org.example;

public class Word {
    private String text;
    private String translation;

    public Word(String text, String translation) {
        this.text = text;
        this.translation = translation;
    }

    @Override
    public String toString() {
        return text + " - " + translation;
    }
}