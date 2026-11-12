package org.example.factory;

public class TranslationExercise extends AbstractTextExercise {

    public TranslationExercise(String sentence, String expectedTranslation) {
        super(sentence, expectedTranslation);
    }

    @Override
    public void displayTask() {
        System.out.println("[Translation] Переведите предложение: " + getPrompt());
    }
}
