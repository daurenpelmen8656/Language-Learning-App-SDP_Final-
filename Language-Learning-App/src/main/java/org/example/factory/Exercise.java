package org.example.factory;

import java.util.List;
import java.util.Locale;

public interface Exercise {
    boolean check(String answer);
    /** Returns the task text so the UI can show prompts and timer context. */
    default String getTaskText() {
        return "Введите ответ";
    }

    default String getType() {
        String suffix = "Exercise";
        return getClass().getSimpleName().replace(suffix, "").toLowerCase(Locale.ROOT);
    }

    default String getQuestion() {
        return getTaskText();
    }

    default List<String> getOptions() {
        return List.of();
    }

    default String getCorrectAnswer() {
        return "";
    }

    /** Compatibility hook for older exercise implementations; rendering belongs to the UI. */
    default void displayTask() {
        throw new UnsupportedOperationException("Exercise text is rendered by the UI");
    }
}
