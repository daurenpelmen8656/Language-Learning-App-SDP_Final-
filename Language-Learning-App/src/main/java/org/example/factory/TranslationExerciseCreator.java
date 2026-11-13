package org.example.factory;

public class TranslationExerciseCreator extends ExerciseCreator {
    @Override
    protected Exercise createExercise(String prompt, String expectedAnswer) {
        return new TranslationExercise(prompt, expectedAnswer);
    }
}
