package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import org.example.factory.ExerciseFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LessonBuilderTest {

    @Test
    void testSuccessfulLessonBuild() {
        Exercise ex1 = ExerciseFactory.create("flashcard", "Apple", "Яблоко");
        Word word1 = new Word("Apple", "Яблоко");

        Lesson lesson = new LessonBuilder()
                .setTitle("Test Lesson")
                .setLevel("A1")
                .addWord(word1)
                .addExercise(ex1)
                .build();

        assertNotNull(lesson, "Урок должен успешно собраться");
        assertEquals(1, lesson.getExercises().size(), "В уроке должно быть 1 упражнение");
        assertEquals(1, lesson.getWords().size(), "В уроке должно быть 1 слово");
    }

    @Test
    void testBuildFailsWithoutTitle() {
        LessonBuilder builder = new LessonBuilder().setLevel("A1");

        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("У урока должны быть название и уровень!", exception.getMessage());
    }

    @Test
    void testBuildFailsWithoutLevel() {
        LessonBuilder builder = new LessonBuilder().setTitle("My Lesson");

        assertThrows(IllegalStateException.class, builder::build);
    }
}