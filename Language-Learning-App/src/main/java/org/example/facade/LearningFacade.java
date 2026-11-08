package org.example.facade;

import org.example.Word;
import org.example.adapter.DictionaryAdapter;
import org.example.adapter.DictionaryService;
import org.example.adapter.ExternalDictionaryApi;
import org.example.builder.Lesson;
import org.example.builder.LessonBuilder;
import org.example.decorator.AudioDecorator;
import org.example.decorator.BasicWordCard;
import org.example.decorator.ExampleDecorator;
import org.example.decorator.ImageHintDecorator;
import org.example.decorator.WordCard;
import org.example.factory.Exercise;
import org.example.factory.ExerciseFactory;
import org.example.observer.ProgressEvent;
import org.example.observer.ProgressPublisher;
import org.example.observer.ReminderService;
import org.example.observer.StreakTracker;
import org.example.observer.XpTracker;
import org.example.singleton.UserSession;
import org.example.singleton.WordProgress;
import org.example.strategy.LeitnerStrategy;
import org.example.strategy.ReviewStrategy;

import java.util.Map;
import java.util.Scanner;

/**
 * Facade Pattern — единая простая точка входа для консольного интерфейса (Main).
 *
 * Интегрирует все остальные паттерны:
 *  - Adapter: получает слова из внешнего словаря
 *  - Decorator: оформляет карточки слов примерами, аудио и картинками
 *  - Factory: создаёт упражнения разных типов (flashcard, translation, quiz)
 *  - Builder: пошагово конструирует объект Lesson
 *  - Strategy: рассчитывает интервалы повторения слов (Leitner, SM-2, Simple)
 *  - Observer: оповещает трекеры XP, Streak и сервис напоминаний
 *  - Singleton: хранит общее состояние сессии пользователя (UserSession)
 */
public class LearningFacade {

    private ReviewStrategy reviewStrategy;
    private final DictionaryService dictionaryService;
    private final ProgressPublisher publisher;

    public LearningFacade() {
        this(new LeitnerStrategy());
    }

    public LearningFacade(ReviewStrategy reviewStrategy) {
        this.reviewStrategy = reviewStrategy;

        // 1. Adapter: подключаем внешний словарь
        this.dictionaryService = new DictionaryAdapter(new ExternalDictionaryApi());

        // 2. Observer: настраиваем издателя событий и регистрируем наблюдателей
        this.publisher = new ProgressPublisher();
        this.publisher.attach(new XpTracker());
        this.publisher.attach(new StreakTracker());
        this.publisher.attach(new ReminderService());
    }

    public void setReviewStrategy(ReviewStrategy reviewStrategy) {
        this.reviewStrategy = reviewStrategy;
        System.out.println("[Facade] Стратегия повторения изменена на: "
                + reviewStrategy.getClass().getSimpleName());
    }

    public ReviewStrategy getReviewStrategy() {
        return reviewStrategy;
    }

    /**
     * Запускает интерактивный урок, собирая его с помощью Adapter, Decorator,
     * Factory и Builder.
     */
    public void startLesson(Scanner scanner) {
        System.out.println("\n=========================================");
        System.out.println("   [Facade] ПОДГОТОВКА И ЗАПУСК УРОКА   ");
        System.out.println("=========================================");

        // Шаг 1: Получаем слова через Adapter
        Word w1 = dictionaryService.findWord("apple");
        Word w2 = dictionaryService.findWord("window");
        Word w3 = dictionaryService.findWord("book");

        // Шаг 2: Украшаем карточки слов через Decorator
        System.out.println("\n--- Карточки слов (паттерн Decorator) ---");
        WordCard card1 = new ExampleDecorator(new BasicWordCard(w1), "An apple a day keeps the doctor away");
        WordCard card2 = new AudioDecorator(new BasicWordCard(w2), "audio/window.mp3");
        WordCard card3 = new ImageHintDecorator(new BasicWordCard(w3), "img/library_book.png");

        System.out.println("1) " + card1.render());
        System.out.println("2) " + card2.render());
        System.out.println("3) " + card3.render());

        // Шаг 3: Создаем упражнения через Factory
        Exercise ex1 = ExerciseFactory.create("flashcard", w1.getText(), w1.getTranslation());
        Exercise ex2 = ExerciseFactory.create("translation", "Open the window, please", "Открой окно, пожалуйста");
        Exercise ex3 = ExerciseFactory.create("quiz", "A) Книга  B) Тетрадь (book = ?)", "A");

        // Шаг 4: Собираем урок пошагово через Builder
        Lesson lesson = new LessonBuilder()
                .setTitle("Урок 1: Базовая лексика и предметы")
                .setLevel("Beginner (A1)")
                .addWord(w1)
                .addWord(w2)
                .addWord(w3)
                .addExercise(ex1)
                .addExercise(ex2)
                .addExercise(ex3)
                .build();

        // Отображаем структуру урока
        lesson.showLessonPlan();

        // Шаг 5: Интерактивное прохождение урока
        System.out.println("--- Начинаем выполнение упражнений ---");

        String[] relatedWords = {w1.getText(), w2.getText(), w3.getText()};
        int index = 0;

        for (Exercise exercise : lesson.getExercises()) {
            System.out.println("\nУпражнение " + (index + 1) + " из " + lesson.getExercises().size() + ":");
            exercise.displayTask();
            System.out.print("Ваш ответ: ");
            String userAnswer = scanner.nextLine();

            boolean isCorrect = exercise.check(userAnswer);
            if (isCorrect) {
                System.out.println("✅ Верно!");
            } else {
                System.out.println("❌ Неверно!");
            }

            // Обрабатываем ответ через Facade (Strategy + Observer + Singleton)
            String wordText = (index < relatedWords.length) ? relatedWords[index] : "general";
            submitAnswer(wordText, isCorrect);
            index++;
        }

        System.out.println("\n🎉 Урок успешно завершен!");
    }

    /**
     * Обрабатывает ответ пользователя:
     * - извлекает текущий прогресс слова из Singleton (UserSession)
     * - рассчитывает следующий интервал повторения через Strategy
     * - обновляет состояние в Singleton
     * - рассылает событие всем подписчикам через Observer
     */
    public void submitAnswer(String wordText, boolean correct) {
        UserSession session = UserSession.getInstance();
        WordProgress progress = session.getProgress(wordText);

        // Расчет интервала через текущую Strategy
        int nextDays = reviewStrategy.nextReviewDays(
                progress.getBox(),
                progress.getConsecutiveCorrect(),
                correct
        );

        // Обновление прогресса
        boolean isFirstTimeLearned = (progress.getBox() == 0 && correct);
        progress.recordAnswer(correct);

        if (isFirstTimeLearned) {
            session.incrementWordsLearned();
        }

        // Оповещение всех подписчиков через Observer
        ProgressEvent event = new ProgressEvent(wordText, correct, nextDays);
        publisher.notifyObservers(event);
    }

    /**
     * Запускает режим быстрого повторения слов.
     */
    public void startReview(Scanner scanner) {
        System.out.println("\n=========================================");
        System.out.println("     [Facade] ПОВТОРЕНИЕ СЛОВ            ");
        System.out.println("   (Алгоритм: " + reviewStrategy.getClass().getSimpleName() + ")");
        System.out.println("=========================================");

        String[] reviewKeys = {"apple", "window", "language", "pattern"};

        for (String key : reviewKeys) {
            Word word = dictionaryService.findWord(key);
            System.out.println("\nКак переводится слово: \"" + word.getText() + "\"?");
            System.out.print("Ваш перевод: ");
            String answer = scanner.nextLine().trim();

            boolean isCorrect = word.getTranslation().equalsIgnoreCase(answer);
            if (isCorrect) {
                System.out.println("✅ Правильно!");
            } else {
                System.out.println("❌ Ошибка! Правильный перевод: " + word.getTranslation());
            }

            submitAnswer(word.getText(), isCorrect);
        }

        System.out.println("\nСессия повторения завершена!");
    }

    /**
     * Показывает общую статистику пользователя из Singleton (UserSession).
     */
    public void showProgress() {
        UserSession session = UserSession.getInstance();

        System.out.println("\n=========================================");
        System.out.println("         ПРОГРЕСС ПОЛЬЗОВАТЕЛЯ           ");
        System.out.println("=========================================");
        System.out.println("Пользователь:       " + session.getUsername());
        System.out.println("Активная стратегия: " + reviewStrategy.getClass().getSimpleName());
        System.out.println("Опыт (XP):          " + session.getXp());
        System.out.println("Текущая серия (🔥): " + session.getStreak());
        System.out.println("Изучено слов:       " + session.getWordsLearned());
        System.out.println("-----------------------------------------");
        System.out.println("Статус повторения слов (Spaced Repetition):");

        Map<String, WordProgress> all = session.getAllProgress();
        if (all.isEmpty()) {
            System.out.println("  (Слова еще не изучались)");
        } else {
            for (Map.Entry<String, WordProgress> entry : all.entrySet()) {
                WordProgress wp = entry.getValue();
                System.out.printf("  • %-12s -> Коробка: %d | Подряд верных: %d%n",
                        entry.getKey(), wp.getBox(), wp.getConsecutiveCorrect());
            }
        }
        System.out.println("=========================================\n");
    }
}
