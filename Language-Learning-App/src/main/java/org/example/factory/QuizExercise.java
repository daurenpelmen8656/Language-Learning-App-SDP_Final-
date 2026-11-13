package org.example.factory;

import java.util.List;

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
    public String getType() {
        return "quiz";
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public List<String> getOptions() {
        return List.of("A) Книга", "B) Тетрадь");
    }

    @Override
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public String getTaskText() {
        return "[Quiz] Выберите правильный вариант: " + question;
    }
}
