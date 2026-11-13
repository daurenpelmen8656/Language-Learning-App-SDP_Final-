package org.example.ui;

/**
 * Настройки таймера ответа на вопросы.
 * Доступные значения: 15, 30, 45, 60 секунд (и отключение).
 */
public final class TimerSettings {

    public static final int[] AVAILABLE_SECONDS = {15, 30, 45, 60};

    private static int seconds = 30;
    private static boolean enabled = true;

    private TimerSettings() {
    }

    public static int getSeconds() {
        return seconds;
    }

    public static void setSeconds(int value) {
        for (int option : AVAILABLE_SECONDS) {
            if (option == value) {
                seconds = value;
                enabled = true;
                return;
            }
        }
        throw new IllegalArgumentException("Недопустимое значение таймера: " + value);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static String describe() {
        if (!enabled) {
            return "выключен";
        }
        return seconds + " сек";
    }
}
