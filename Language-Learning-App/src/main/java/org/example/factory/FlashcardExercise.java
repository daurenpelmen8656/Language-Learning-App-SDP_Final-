package org.example.factory;

public class FlashcardExercise implements Exercise {
    private final String word;
    private final String expectedTranslation;

    public FlashcardExercise(String word, String expectedTranslation) {
        this.word = word;
        this.expectedTranslation = expectedTranslation;
    }

    @Override
    public boolean check(String answer) {
        return expectedTranslation.equalsIgnoreCase(answer.trim());
    }

    @Override
    public void displayTask() {
        System.out.println("[Flashcard] Как переводится слово: " + word + "?");
    }
}
