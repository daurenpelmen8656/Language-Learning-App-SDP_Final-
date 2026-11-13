package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Lesson {
    private final String title;
    private final String level;
    private final List<Word> words;
    private final List<Exercise> exercises;

    // Package visibility keeps lesson creation within LessonBuilder.
    Lesson(String title, String level, List<Word> words, List<Exercise> exercises) {
        this.title = title;
        this.level = level;
        this.words = Collections.unmodifiableList(new ArrayList<>(words));
        this.exercises = Collections.unmodifiableList(new ArrayList<>(exercises));
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
