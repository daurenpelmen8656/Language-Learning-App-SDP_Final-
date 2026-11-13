package org.example.factory;

public class QuizExerciseCreator extends ExerciseCreator {
    @Override
    protected Exercise createExercise(String prompt, String expectedAnswer) {
        return new QuizExercise(prompt, expectedAnswer);
    }
}
