package org.example.facade;

import org.example.factory.Exercise;

import java.util.List;

public record LessonView(String title, String level, List<WordView> words,
                         List<CardView> cards, List<Exercise> exercises) {
    public LessonView {
        words = List.copyOf(words);
        cards = List.copyOf(cards);
        exercises = List.copyOf(exercises);
    }
}
