package org.example.factory;

public class FlashcardExercise extends AbstractTextExercise {
    public FlashcardExercise(String word, String expectedTranslation) {
        super(word, expectedTranslation);
    }

    @Override
    public String getTaskText() {
        return "[Flashcard] Как переводится слово: " + getPrompt() + "?";
    }
}
