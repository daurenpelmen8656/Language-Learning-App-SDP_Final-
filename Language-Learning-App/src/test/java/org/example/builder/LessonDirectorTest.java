package org.example.builder;

import org.example.Word;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LessonDirectorTest {

    private final List<Word> words = List.of(new Word("apple", "яблоко"), new Word("book", "книга"));
    private final LessonDirector director = new LessonDirector();

    @Test
    void vocabularyLessonHasOneFlashcardPerWord() {
        Lesson lesson = director.vocabularyLesson("Basics", "A1", words);

        assertEquals("Basics", lesson.getTitle());
        assertEquals("A1", lesson.getLevel());
        assertEquals(2, lesson.getWords().size());
        assertEquals(2, lesson.getExercises().size());
        assertTrue(lesson.getExercises().get(0).check("яблоко"));
    }

    @Test
    void mixedLessonAddsTranslationExercises() {
        Map<String, String> sentences = new LinkedHashMap<>();
        sentences.put("Open the window", "Открой окно");

        Lesson lesson = director.mixedLesson("Mixed", "A1", words, sentences);

        assertEquals(3, lesson.getExercises().size());
        assertTrue(lesson.getExercises().get(2).check("открой окно"));
    }

    @Test
    void directorWithoutWordsFailsValidation() {
        assertThrows(IllegalStateException.class, () -> director.vocabularyLesson("Empty", "A1", List.of()));
    }
}
