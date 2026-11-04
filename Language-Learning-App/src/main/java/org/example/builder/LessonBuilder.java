package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import java.util.ArrayList;
import java.util.List;

public class LessonBuilder {
    private String title;
    private String level;
    private final List<Word> words = new ArrayList<>();
    private final List<Exercise> exercises = new ArrayList<>();

    public LessonBuilder setTitle(String title) {
        this.title = title;
        return this; // Возвращаем текущий объект для цепочки вызовов
    }

    public LessonBuilder setLevel(String level) {
        this.level = level;
        return this;
    }

    public LessonBuilder addWord(Word word) {
        this.words.add(word);
        return this;
    }

    public LessonBuilder addExercise(Exercise exercise) {
        this.exercises.add(exercise);
        return this;
    }

    public Lesson build() {
        if (title == null || level == null) {
            throw new IllegalStateException("У урока должны быть название и уровень!");
        }
        return new Lesson(title, level, words, exercises);
    }
}