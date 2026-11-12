package org.example.factory;

public class FlashcardExerciseCreator extends ExerciseCreator {
    @Override
    protected Exercise createExercise(String prompt, String expectedAnswer) {
        return new FlashcardExercise(prompt, expectedAnswer);
    }
}
