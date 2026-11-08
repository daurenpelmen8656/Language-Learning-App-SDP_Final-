package org.example.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExerciseFactoryTest {

    @Test
    void testCreateFlashcardExercise() {
        Exercise exercise = ExerciseFactory.create("flashcard", "Apple", "Яблоко");
        assertNotNull(exercise, "Упражнение не должно быть null");
        assertTrue(exercise instanceof FlashcardExercise, "Должен создаваться объект FlashcardExercise");
        assertTrue(exercise.check("Яблоко"), "Проверка правильного ответа должна возвращать true");
        assertFalse(exercise.check("Груша"), "Проверка неверного ответа должна возвращать false");
    }

    @Test
    void testCreateQuizExercise() {
        Exercise exercise = ExerciseFactory.create("quiz", "A) Окно B) Дверь (Window = ?)", "A");
        assertTrue(exercise instanceof QuizExercise, "Должен создаваться объект QuizExercise");
        assertTrue(exercise.check("A"));
    }

    @Test
    void testCreateTranslationExercise() {
        Exercise exercise = ExerciseFactory.create("translation", "I like Java", "Мне нравится Java");
        assertTrue(exercise instanceof TranslationExercise);
    }

    @Test
    void testUnknownExerciseTypeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ExerciseFactory.create("unknown_type", "Prompt", "Answer");
        });
        String expectedMessage = "Неизвестный тип упражнения: unknown_type";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}