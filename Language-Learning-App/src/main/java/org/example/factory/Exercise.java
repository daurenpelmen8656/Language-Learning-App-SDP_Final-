package org.example.factory;

import java.util.List;

public interface Exercise {
    boolean check(String answer);
    String getType();
    String getQuestion();
    String getCorrectAnswer();
    default List<String> getOptions() {
        return List.of();
    }
    /** Returns the task text so the UI can show prompts and timer context. */
    default String getTaskText() {
        return "Введите ответ";
    }
}
