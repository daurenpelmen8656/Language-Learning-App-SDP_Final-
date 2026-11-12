package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import java.util.List;

/** Product of the Builder pattern. Immutable: lists are copied and unmodifiable. */
public class Lesson {
    private final String title;
    private final String level;
    private final List<Word> words;
    private final List<Exercise> exercises;

    // Package-private: only LessonBuilder may create a Lesson
    Lesson(String title, String level, List<Word> words, List<Exercise> exercises) {
        this.title = title;
        this.level = level;
        this.words = List.copyOf(words);
        this.exercises = List.copyOf(exercises);
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

    public List<Word> getWords() {
        return words;
    }

    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }
}
