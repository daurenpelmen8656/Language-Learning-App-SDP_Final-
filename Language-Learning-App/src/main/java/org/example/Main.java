package org.example;

import org.example.builder.Lesson;
import org.example.builder.LessonBuilder;
import org.example.factory.Exercise;
import org.example.factory.ExerciseFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("1. Создаем упражнения через Factory...");
        Exercise ex1 = ExerciseFactory.create("flashcard", "Apple", "Яблоко");
        Exercise ex2 = ExerciseFactory.create("translation", "I like Java", "Мне нравится Java");
        Exercise ex3 = ExerciseFactory.create("quiz", "A) Окно B) Дверь (Window = ?)", "A");

        System.out.println("2. Собираем урок через Builder...");
        Lesson lesson = new LessonBuilder()
                .setTitle("Основы программирования и фрукты")
                .setLevel("Beginner (A1)")
                .addWord(new Word("Apple", "Яблоко"))
                .addWord(new Word("Window", "Окно"))
                .addExercise(ex1)
                .addExercise(ex2)
                .addExercise(ex3)
                .build();

        // Показываем структуру урока
        lesson.showLessonPlan();

        // Интерактивная проверка упражнений
        Scanner scanner = new Scanner(System.in);
        for (Exercise ex : lesson.getExercises()) {
            ex.displayTask();
            System.out.print("Ваш ответ: ");
            String answer = scanner.nextLine();

            if (ex.check(answer)) {
                System.out.println("✅ Правильно!\n");
            } else {
                System.out.println("❌ Неверно.\n");
            }
        }
        scanner.close();
    }
}