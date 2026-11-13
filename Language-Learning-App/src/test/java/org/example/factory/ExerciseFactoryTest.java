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
    void testTypeIsCaseInsensitive() {
        assertTrue(ExerciseFactory.create("FlashCard", "Apple", "Яблоко") instanceof FlashcardExercise);
    }

    @Test
    void testAnswerCheckIsTrimmedCaseInsensitiveAndNullSafe() {
        Exercise exercise = ExerciseFactory.create("flashcard", "Apple", "Яблоко");
        assertTrue(exercise.check("  яблоко  "));
        assertFalse(exercise.check(null));
    }

    @Test
    void testUnknownExerciseTypeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ExerciseFactory.create("unknown_type", "Prompt", "Answer");
        });
        String expectedMessage = "Неизвестный тип упражнения: unknown_type";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testNullTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ExerciseFactory.create(null, "P", "A"));
    }

    @Test
    void testBlankPromptOrAnswerIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ExerciseFactory.create("quiz", " ", "A"));
        assertThrows(IllegalArgumentException.class, () -> ExerciseFactory.create("quiz", "Q", null));
    }

    @Test
    void testNewTypeCanBeRegisteredWithoutChangingFactory() {
        // Open/Closed: a brand-new exercise type, factory code is untouched
        class MatchingExercise extends AbstractTextExercise {
            MatchingExercise(String prompt, String answer) {
                super(prompt, answer);
            }

            @Override
            public void displayTask() {
                System.out.println("[Matching] " + getPrompt());
            }
        }
        ExerciseFactory factory = ExerciseFactory.withDefaults().register("matching", new ExerciseCreator() {
            @Override
            protected Exercise createExercise(String prompt, String expectedAnswer) {
                return new MatchingExercise(prompt, expectedAnswer);
            }
        });

        Exercise exercise = factory.createExercise("matching", "cat - ?", "кошка");
        assertTrue(exercise instanceof MatchingExercise);
        assertTrue(factory.supportedTypes().contains("matching"));
        // the default factory is not affected
        assertThrows(IllegalArgumentException.class, () -> ExerciseFactory.create("matching", "p", "a"));
    }
}
