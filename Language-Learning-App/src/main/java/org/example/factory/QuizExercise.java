package org.example.factory;

public class QuizExercise extends AbstractTextExercise {

    public QuizExercise(String question, String correctAnswer) {
        super(question, correctAnswer);
    }

    @Override
    public void displayTask() {
        System.out.println("[Quiz] Выберите правильный вариант: " + getPrompt());
    }
}
