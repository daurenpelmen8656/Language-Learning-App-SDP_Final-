package org.example.factory;

public class QuizExercise implements Exercise {
    private final String question;
    private final String correctAnswer;

    public QuizExercise(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean check(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }

    @Override
    public String getTaskText() {
        return "[Quiz] Выберите правильный вариант: " + question;
    }
}
