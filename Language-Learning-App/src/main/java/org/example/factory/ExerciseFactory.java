package org.example.factory;

public class ExerciseFactory {
    // Фабричный метод
    public static Exercise create(String type, String prompt, String expectedAnswer) {
        switch (type.toLowerCase()) {
            case "flashcard":
                return new FlashcardExercise(prompt, expectedAnswer);
            case "translation":
                return new TranslationExercise(prompt, expectedAnswer);
            case "quiz":
                return new QuizExercise(prompt, expectedAnswer);
            default:
                throw new IllegalArgumentException("Неизвестный тип упражнения: " + type);
        }
    }
}