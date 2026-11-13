package org.example.ui;

import java.util.Optional;
import java.util.Scanner;

/**
 * Единая точка ввода ответа: JavaFX-таймер или консоль (если таймер выключен).
 */
public final class AnswerInput {

    private AnswerInput() {
    }

    /**
     * @return ответ пользователя, или empty если время вышло / пропуск
     */
    public static Optional<String> read(Scanner scanner, String title, String prompt) {
        if (TimerSettings.isEnabled()) {
            System.out.println("⏱  Откройте окно таймера и введите ответ ("
                    + TimerSettings.getSeconds() + " сек)...");
            Optional<String> answer = TimedAnswerDialog.ask(title, prompt, TimerSettings.getSeconds());
            if (answer.isEmpty()) {
                System.out.println("⏰ Время вышло или ответ пропущен.");
            } else {
                System.out.println("Ваш ответ: " + answer.get());
            }
            return answer;
        }

        System.out.print("Ваш ответ: ");
        String line = scanner.nextLine();
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(line.trim());
    }
}
