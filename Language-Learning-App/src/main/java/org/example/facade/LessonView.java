package org.example.facade;

import org.example.factory.Exercise;

import java.util.List;

public record LessonView(String title, String level, List<WordView> words,
                         List<String> renderedCards, List<Exercise> exercises) {
    public LessonView {
        words = List.copyOf(words);
        renderedCards = List.copyOf(renderedCards);
        exercises = List.copyOf(exercises);
    }
}
