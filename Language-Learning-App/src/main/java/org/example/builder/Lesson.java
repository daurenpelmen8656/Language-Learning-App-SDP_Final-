package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import java.util.List;

public class Lesson {
    private final String title;
    private final String level;
    private final List<Word> words;
    private final List<Exercise> exercises;

    // Конструктор доступен только внутри пакета (для LessonBuilder)
    Lesson(String title, String level, List<Word> words, List<Exercise> exercises) {
        this.title = title;
        this.level = level;
        this.words = words;
        this.exercises = exercises;
    }

    public void showLessonPlan() {
        System.out.println("=== Урок: " + title + " (Уровень: " + level + ") ===");
        System.out.println("Новые слова:");
        for (Word w : words) {
            System.out.println(" - " + w.toString());
        }
        System.out.println("\nКоличество упражнений: " + exercises.size());
        System.out.println("=========================================\n");
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}