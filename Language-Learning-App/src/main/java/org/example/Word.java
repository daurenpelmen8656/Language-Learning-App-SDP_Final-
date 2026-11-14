package org.example;

/** English vocabulary word with Russian and Kazakh translations. */
public record Word(String text, String translation, String kazakhTranslation, String level) {

    public Word {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Слово не может быть пустым");
        }
        if (translation == null || translation.isBlank()) {
            throw new IllegalArgumentException("Перевод не может быть пустым");
        }
        if (kazakhTranslation == null || kazakhTranslation.isBlank()) {
            throw new IllegalArgumentException("Казахский перевод не может быть пустым");
        }
    }

    /** Creates a word without a level. */
    public Word(String text, String translation) {
        this(text, translation, translation, null);
    }

    /** Creates a Russian-translated word with an optional level. */
    public Word(String text, String translation, String level) {
        this(text, translation, translation, level);
    }

    public String getText() {
        return text;
    }

    public String getTranslation() {
        return translation;
    }

    public String getKazakhTranslation() {
        return kazakhTranslation;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        if (level != null && !level.isBlank()) {
            return text + " - " + translation + " [" + level + "]";
        }
        return text + " - " + translation;
    }
}
