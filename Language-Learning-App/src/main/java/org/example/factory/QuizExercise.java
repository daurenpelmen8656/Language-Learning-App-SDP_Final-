package org.example.factory;

public class QuizExercise extends AbstractTextExercise {
    public QuizExercise(String question, String correctAnswer) {
        super(question, correctAnswer);
    }

    @Override
    public String getTaskText() {
        return "[Quiz] Выберите правильный вариант: " + getPrompt();
    }
}
