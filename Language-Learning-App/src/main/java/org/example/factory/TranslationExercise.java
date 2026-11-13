package org.example.factory;

public class TranslationExercise extends AbstractTextExercise {
    public TranslationExercise(String sentence, String expectedTranslation) {
        super(sentence, expectedTranslation);
    }

    @Override
    public String getTaskText() {
        return "[Translation] Переведите предложение: " + getPrompt();
    }
}


