package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Builder: assembles a {@link Lesson} step by step and validates it in {@link #build()}. */
public class LessonBuilder {
    private String title;
    private String level;
    private final List<Word> words = new ArrayList<>();
    private final List<Exercise> exercises = new ArrayList<>();

    public LessonBuilder setTitle(String title) {
        this.title = title;
        return this; // fluent chain
    }

    public LessonBuilder setLevel(String level) {
        this.level = level;
        return this;
    }

    public LessonBuilder addWord(Word word) {
        this.words.add(Objects.requireNonNull(word, "Слово не может быть null"));
        return this;
    }

    public LessonBuilder addWords(List<Word> words) {
        Objects.requireNonNull(words, "Список слов не может быть null").forEach(this::addWord);
        return this;
    }

    public LessonBuilder addExercise(Exercise exercise) {
        this.exercises.add(Objects.requireNonNull(exercise, "Упражнение не может быть null"));
        return this;
    }

    public LessonBuilder addExercises(List<Exercise> exercises) {
        Objects.requireNonNull(exercises, "Список упражнений не может быть null").forEach(this::addExercise);
        return this;
    }

    /**
     * Validates the collected data and creates the lesson.
     *
     * @throws IllegalStateException if the title/level is missing, or the lesson has no words or exercises
     */
    public Lesson build() {
        if (isBlank(title) || isBlank(level)) {
            throw new IllegalStateException("У урока должны быть название и уровень!");
        }
        if (words.isEmpty()) {
            throw new IllegalStateException("В уроке должно быть хотя бы одно слово!");
        }
        if (exercises.isEmpty()) {
            throw new IllegalStateException("В уроке должно быть хотя бы одно упражнение!");
        }
        return new Lesson(title, level, words, exercises);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
