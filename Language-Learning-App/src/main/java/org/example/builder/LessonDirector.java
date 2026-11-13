package org.example.builder;

import org.example.Word;
import org.example.factory.ExerciseFactory;
import java.util.List;
import java.util.Map;

/**
 * Director of the Builder pattern: knows ready-made "recipes" for lessons,
 * so callers do not repeat the same sequence of builder calls.
 */
public class LessonDirector {
    private final ExerciseFactory factory;

    public LessonDirector() {
        this(ExerciseFactory.withDefaults());
    }

    public LessonDirector(ExerciseFactory factory) {
        this.factory = factory;
    }

    /** Vocabulary lesson: one flashcard for every word. */
    public Lesson vocabularyLesson(String title, String level, List<Word> words) {
        LessonBuilder builder = new LessonBuilder().setTitle(title).setLevel(level);
        for (Word word : words) {
            builder.addWord(word)
                   .addExercise(factory.createExercise("flashcard", word.text(), word.translation()));
        }
        return builder.build();
    }

    /**
     * Mixed lesson: a flashcard for every word, plus translation
     * exercises for the given sentences (sentence -> expected translation).
     */
    public Lesson mixedLesson(String title, String level, List<Word> words, Map<String, String> sentences) {
        LessonBuilder builder = new LessonBuilder().setTitle(title).setLevel(level);
        for (Word word : words) {
            builder.addWord(word)
                   .addExercise(factory.createExercise("flashcard", word.text(), word.translation()));
        }
        sentences.forEach((sentence, translation) ->
                builder.addExercise(factory.createExercise("translation", sentence, translation)));
        return builder.build();
    }
}
