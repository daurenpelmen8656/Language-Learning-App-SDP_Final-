package org.example.factory;

public class TranslationExercise implements Exercise {
    private final String sentence;
    private final String expectedTranslation;

    public TranslationExercise(String sentence, String expectedTranslation) {
        this.sentence = sentence;
        this.expectedTranslation = expectedTranslation;
    }

    @Override
    public boolean check(String answer) {
        return expectedTranslation.equalsIgnoreCase(answer.trim());
    }

    @Override
    public void displayTask() {
        System.out.println("[Translation] Переведите предложение: " + sentence);
    }
}


