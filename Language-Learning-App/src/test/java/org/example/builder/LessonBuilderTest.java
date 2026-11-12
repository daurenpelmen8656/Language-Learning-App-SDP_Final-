package org.example.builder;

import org.example.Word;
import org.example.factory.Exercise;
import org.example.factory.ExerciseFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LessonBuilderTest {

    private final Word apple = new Word("Apple", "Яблоко");
    private final Exercise flashcard = ExerciseFactory.create("flashcard", "Apple", "Яблоко");

    @Test
    void testSuccessfulLessonBuild() {
        Lesson lesson = new LessonBuilder()
                .setTitle("Test Lesson")
                .setLevel("A1")
                .addWord(apple)
                .addExercise(flashcard)
                .build();

        assertNotNull(lesson, "Урок должен успешно собраться");
        assertEquals(1, lesson.getExercises().size(), "В уроке должно быть 1 упражнение");
        assertEquals(1, lesson.getWords().size(), "В уроке должно быть 1 слово");
    }

    @Test
    void testBuildFailsWithoutTitle() {
        LessonBuilder builder = new LessonBuilder().setLevel("A1").addWord(apple).addExercise(flashcard);

        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("У урока должны быть название и уровень!", exception.getMessage());
    }

    @Test
    void testBuildFailsWithoutLevel() {
        LessonBuilder builder = new LessonBuilder().setTitle("My Lesson").addWord(apple).addExercise(flashcard);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testBuildFailsWithBlankTitle() {
        LessonBuilder builder = new LessonBuilder().setTitle("  ").setLevel("A1").addWord(apple).addExercise(flashcard);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testBuildFailsWithoutWords() {
        LessonBuilder builder = new LessonBuilder().setTitle("T").setLevel("A1").addExercise(flashcard);

        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("слово"));
    }

    @Test
    void testBuildFailsWithoutExercises() {
        LessonBuilder builder = new LessonBuilder().setTitle("T").setLevel("A1").addWord(apple);

        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(exception.getMessage().contains("упражнение"));
    }

    @Test
    void testNullElementsAreRejected() {
        assertThrows(NullPointerException.class, () -> new LessonBuilder().addWord(null));
        assertThrows(NullPointerException.class, () -> new LessonBuilder().addExercise(null));
    }

    @Test
    void testLessonIsImmutableAndIndependentOfBuilder() {
        LessonBuilder builder = new LessonBuilder().setTitle("T").setLevel("A1").addWord(apple).addExercise(flashcard);
        Lesson lesson = builder.build();

        builder.addWord(new Word("Book", "Книга"));
        assertEquals(1, lesson.getWords().size(), "Дальнейшие изменения builder не должны менять урок");
        assertThrows(UnsupportedOperationException.class, () -> lesson.getWords().add(apple));
        assertThrows(UnsupportedOperationException.class, () -> lesson.getExercises().add(flashcard));
    }
}
