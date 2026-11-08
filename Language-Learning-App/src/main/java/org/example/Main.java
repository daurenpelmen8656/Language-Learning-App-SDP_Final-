package org.example;

import org.example.facade.LearningFacade;
import org.example.strategy.LeitnerStrategy;
import org.example.strategy.SimpleIntervalStrategy;
import org.example.strategy.Sm2Strategy;

import java.util.Scanner;

/**
 * Main — главная точка входа в консольное приложение.
 *
 * Предоставляет меню и управляет процессом исключительно через LearningFacade,
 * демонстрируя интеграцию всех 8 паттернов проектирования.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LearningFacade facade = new LearningFacade();

        System.out.println("==================================================");
        System.out.println("  Language Learning App (Java) — SDP Final Project");
        System.out.println("  Команда: Dauren, Yelzhan, Turar, Nurym (SE-2535)");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            System.out.println("\nГЛАВНОЕ МЕНЮ:");
            System.out.println("1. Начать урок (Lesson: Builder + Factory + Decorator + Adapter)");
            System.out.println("2. Повторение слов (Review: Strategy + Observer + Singleton)");
            System.out.println("3. Мой прогресс (Progress: Singleton UserSession)");
            System.out.println("4. Сменить алгоритм повторения (Паттерн Strategy)");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> facade.startLesson(scanner);
                case "2" -> facade.startReview(scanner);
                case "3" -> facade.showProgress();
                case "4" -> changeStrategyMenu(scanner, facade);
                case "0" -> {
                    System.out.println("\nСпасибо за занятие! До встречи!");
                    running = false;
                }
                default -> System.out.println("Неверный ввод. Пожалуйста, выберите пункт от 0 до 4.");
            }
        }

        scanner.close();
    }

    private static void changeStrategyMenu(Scanner scanner, LearningFacade facade) {
        System.out.println("\n--- Выбор алгоритма повторения (Strategy Pattern) ---");
        System.out.println("Текущий алгоритм: " + facade.getReviewStrategy().getClass().getSimpleName());
        System.out.println("1. Leitner System (Коробки с интервалами 1, 2, 4, 8, 16, 30 дней)");
        System.out.println("2. SuperMemo-2 (SM-2: Экспоненциальный рост интервалов с фактором 2.5)");
        System.out.println("3. Simple Interval (Линейный рост: +2 дня за каждый верный ответ)");
        System.out.println("0. Отмена");
        System.out.print("Выберите стратегию: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> facade.setReviewStrategy(new LeitnerStrategy());
            case "2" -> facade.setReviewStrategy(new Sm2Strategy());
            case "3" -> facade.setReviewStrategy(new SimpleIntervalStrategy());
            case "0" -> System.out.println("Смена алгоритма отменена.");
            default -> System.out.println("Неверный выбор.");
        }
    }
}
