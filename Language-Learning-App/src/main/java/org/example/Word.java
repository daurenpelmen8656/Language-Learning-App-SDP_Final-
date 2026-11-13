package org.example;

/**
 * Immutable model of a word in the language learning app.
 *
 * <p>Implemented as a record: all fields are final and there are no setters.
 * The {@code getX()} methods are kept so that existing code
 * (decorators, adapter, facade) keeps working unchanged.</p>
 *
 * @param text        the word in the target language, never blank
 * @param translation the translation, never blank
 * @param kazakhTranslation Kazakh translation, never blank
 * @param level       optional CEFR level (A1, B2, ...), may be {@code null}
 */
public record Word(String text, String translation, String kazakhTranslation, String level) {

    /** Compact constructor: validates the invariants of every Word. */
    public Word {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Слово не может быть пустым");
        }
        if (translation == null || translation.isBlank()) {
            throw new IllegalArgumentException("Перевод не может быть пустым");
        }
        if (kazakhTranslation == null || kazakhTranslation.isBlank()) {
            throw new IllegalArgumentException("Kazakh translation must not be blank");
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
