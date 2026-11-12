package org.example.factory;

/**
 * Base class for exercises whose answer is a short text.
 * Answer checking lives here once (null-safe, trimmed, case-insensitive),
 * so concrete exercises only decide how the task is displayed (DRY).
 */
public abstract class AbstractTextExercise implements Exercise {
    private final String prompt;
    private final String expectedAnswer;

    protected AbstractTextExercise(String prompt, String expectedAnswer) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Текст задания не может быть пустым");
        }
        if (expectedAnswer == null || expectedAnswer.isBlank()) {
            throw new IllegalArgumentException("Правильный ответ не может быть пустым");
        }
        this.prompt = prompt;
        this.expectedAnswer = expectedAnswer;
    }

    @Override
    public boolean check(String answer) {
        return answer != null && expectedAnswer.equalsIgnoreCase(answer.trim());
    }

    protected String getPrompt() {
        return prompt;
    }
}
