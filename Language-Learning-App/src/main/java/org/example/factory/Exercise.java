package org.example.factory;

public interface Exercise {
    boolean check(String answer);
    /** Returns the task text so the UI can show prompts and timer context. */
    default String getTaskText() {
        return "Введите ответ";
    }
}
