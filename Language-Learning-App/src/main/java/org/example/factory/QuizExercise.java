package org.example.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizExercise extends AbstractTextExercise {
    private static final Pattern OPTION_PATTERN = Pattern.compile("([A-Z])\\)\\s*([^\\s]+)");

    public QuizExercise(String question, String correctAnswer) {
        super(question, correctAnswer);
    }

    @Override
    public String getTaskText() {
        return "[Quiz] Выберите правильный вариант: " + getPrompt();
    }

    @Override
    public List<String> getOptions() {
        Matcher matcher = OPTION_PATTERN.matcher(getQuestion());
        List<String> options = new ArrayList<>();
        while (matcher.find()) {
            options.add(matcher.group(1) + ") " + matcher.group(2));
        }
        return List.copyOf(options);
    }
}
