package org.example;

/**
 * Common model representing a word in the language learning app.
 */
public class Word {
    private String text;
    private String translation;
    private String level; // e.g., "A1", "A2", "B1", "B2", "C1", "C2"

    public Word(String text, String translation, String level) {
        this.text = text;
        this.translation = translation;
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return text + " — " + translation + " [" + level + "]";
    }
}
