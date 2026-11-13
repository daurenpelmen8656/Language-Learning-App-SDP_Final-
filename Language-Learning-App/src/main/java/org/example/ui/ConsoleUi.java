package org.example.ui;

import org.example.facade.AnswerResult;
import org.example.facade.LearningFacade;
import org.example.facade.LessonView;
import org.example.facade.ProgressView;
import org.example.factory.Exercise;
import org.example.facade.WordView;
import org.example.facade.WordProgressView;
import org.example.strategy.ReviewStrategy;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public final class ConsoleUi {
    public static final String LEITNER_CHOICE = "1";
    public static final String SM2_CHOICE = "2";
    public static final String SIMPLE_INTERVAL_CHOICE = "3";
    private static final String APP_TITLE = "Language Learning App (Java) — SDP Final Project";
    private static final String TEAM_LINE = "Team: Yelzhan, Dauren, Turar     Group:(SE-2535)";
    private static final String[] STRATEGY_NAMES = {"Leitner System", "SuperMemo-2", "Simple Interval"};
    private final LearningFacade facade;
    private final Map<String, ReviewStrategy> availableStrategies;

    public ConsoleUi(LearningFacade facade, Map<String, ReviewStrategy> availableStrategies) {
        this.facade = Objects.requireNonNull(facade);
        this.availableStrategies = Map.copyOf(availableStrategies);
    }

    public void run() {
        try {
            StartupSplash.showAndWait();
        } catch (Exception exception) {
            System.out.println("[UI] Не удалось показать splash: " + exception.getMessage());
        }
        try (Scanner scanner = new Scanner(System.in)) {
            printBanner();
            boolean running = true;
            while (running) {
                printMainMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> startLesson(scanner);
                    case "2" -> startReview(scanner);
                    case "3" -> showProgress();
                    case "4" -> changeStrategy(scanner);
                    case "5" -> timerSettings(scanner);
                    case "0" -> {
                        System.out.println("\nСпасибо за занятие! До встречи!");
                        running = false;
                    }
                    default -> System.out.println("Неверный ввод. Пожалуйста, выберите пункт от 0 до 5.");
                }
            }
        }
    }

    private void startLesson(Scanner scanner) {
        LessonView lesson = facade.buildLesson();
        System.out.println("\n=== Урок: " + lesson.title() + " (Уровень: " + lesson.level() + ") ===");
        System.out.println("Новые слова:");
        lesson.words().forEach(word -> System.out.println(" - " + word));
        System.out.println("\nКарточки слов:");
        for (String card : lesson.renderedCards()) {
            System.out.println(card);
        }
        int exerciseNumber = 0;
        for (Exercise exercise : lesson.exercises()) {
            exerciseNumber++;
            String prompt = exercise.getTaskText();
            System.out.println("\nУпражнение " + exerciseNumber + " из " + lesson.exercises().size() + ": " + prompt);
            Optional<String> answer = AnswerInput.read(scanner, "Урок · упражнение " + exerciseNumber, prompt);
            boolean correct = answer.isPresent() && exercise.check(answer.get());
            showAnswer(correct, answer.isEmpty());
            String wordText = exerciseNumber <= lesson.words().size()
                    ? lesson.words().get(exerciseNumber - 1).text() : "general";
            showAnswerResult(facade.submitAnswer(wordText, correct));
        }
        System.out.println("\n🎉 Урок успешно завершен!");
    }

    private void startReview(Scanner scanner) {
        Map<String, WordProgressView> dueWords = facade.getDueWords();
        System.out.println("\nПОВТОРЕНИЕ СЛОВ (" + facade.getReviewStrategy().getClass().getSimpleName() + ")");
        if (dueWords.isEmpty()) {
            System.out.println("Нет слов, запланированных к повторению на сегодня.");
            return;
        }
        for (String wordText : dueWords.keySet()) {
            WordView word = facade.findWord(wordText);
            String prompt = "Как переводится слово: \"" + word.text() + "\"?";
            Optional<String> answer = AnswerInput.read(scanner, "Повторение слов", prompt);
            boolean correct = answer.isPresent() && word.translation().equalsIgnoreCase(answer.get());
            showAnswer(correct, answer.isEmpty());
            showAnswerResult(facade.submitAnswer(word.text(), correct));
        }
        System.out.println("\nСессия повторения завершена!");
    }

    private void showAnswer(boolean correct, boolean skipped) {
        if (skipped) {
            System.out.println("❌ Нет ответа (таймаут / пропуск).");
        } else if (correct) {
            System.out.println("✅ Верно!");
        } else {
            System.out.println("❌ Неверно!");
        }
    }

    private void showAnswerResult(AnswerResult result) {
        System.out.println("Повторение запланировано на " + result.nextReviewDate() + ". XP: "
                + result.xp() + ", серия: " + result.streak() + ".");
    }

    private void showProgress() {
        ProgressView progress = facade.getProgress();
        System.out.println("\nПРОГРЕСС ПОЛЬЗОВАТЕЛЯ");
        System.out.println("Пользователь:       " + progress.username());
        System.out.println("Активная стратегия: " + progress.strategyName());
        System.out.println("Опыт (XP):          " + progress.xp());
        System.out.println("Текущая серия (🔥): " + progress.streak());
        System.out.println("Изучено слов:       " + progress.wordsLearned());
        System.out.println("Статус повторения слов:");
        if (progress.words().isEmpty()) {
            System.out.println("  (Слова еще не изучались)");
        } else {
            progress.words().forEach((word, wordProgress) -> System.out.printf(
                    "  • %-12s -> Коробка: %d | Подряд верных: %d | Повтор: %s%n",
                    word, wordProgress.box(), wordProgress.consecutiveCorrect(), wordProgress.nextReviewDate()));
        }
    }

    private void changeStrategy(Scanner scanner) {
        System.out.println("\n--- Выбор алгоритма повторения ---");
        System.out.println("Текущий алгоритм: " + facade.getReviewStrategy().getClass().getSimpleName());
        System.out.println("1. " + STRATEGY_NAMES[0]);
        System.out.println("2. " + STRATEGY_NAMES[1]);
        System.out.println("3. " + STRATEGY_NAMES[2]);
        System.out.println("0. Отмена");
        ReviewStrategy selectedStrategy = availableStrategies.get(scanner.nextLine().trim());
        if (selectedStrategy == null) {
            System.out.println("Смена стратегии не выполнена.");
            return;
        }
        facade.setReviewStrategy(selectedStrategy);
    }

    private void timerSettings(Scanner scanner) {
        System.out.println("\n--- Настройки таймера ответа (JavaFX) ---");
        System.out.println("Текущее значение: " + TimerSettings.describe());
        System.out.println("1. 15 секунд\n2. 30 секунд\n3. 45 секунд\n4. 60 секунд\n5. Выключить таймер\n0. Отмена");
        switch (scanner.nextLine().trim()) {
            case "1" -> TimerSettings.setSeconds(TimerSettings.AVAILABLE_SECONDS[0]);
            case "2" -> TimerSettings.setSeconds(TimerSettings.AVAILABLE_SECONDS[1]);
            case "3" -> TimerSettings.setSeconds(TimerSettings.AVAILABLE_SECONDS[2]);
            case "4" -> TimerSettings.setSeconds(TimerSettings.AVAILABLE_SECONDS[3]);
            case "5" -> TimerSettings.setEnabled(false);
            default -> { return; }
        }
        System.out.println("Настройки таймера обновлены: " + TimerSettings.describe());
    }

    private void printBanner() {
        System.out.println("\n==================================================");
        System.out.println("  " + APP_TITLE);
        System.out.println("  " + TEAM_LINE);
        System.out.println("==================================================");
        System.out.println("  Таймер ответов: " + TimerSettings.describe());
    }

    private void printMainMenu() {
        System.out.println("\nГЛАВНОЕ МЕНЮ:");
        System.out.println("1. Начать урок\n2. Повторение слов\n3. Мой прогресс\n4. Сменить алгоритм повторения\n5. Настройки таймера\n0. Выход");
        System.out.print("Выберите действие: ");
        System.out.flush();
    }
}
