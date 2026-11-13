package org.example.factory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Registry of {@link ExerciseCreator}s (Factory Method + registry).
 *
 * <p>There is no {@code switch}: a new exercise type is added by registering
 * a new creator, and this class is not edited (Open/Closed Principle).</p>
 *
 * <pre>{@code
 * ExerciseFactory factory = ExerciseFactory.withDefaults()
 *         .register("matching", new MatchingExerciseCreator());
 * Exercise e = factory.createExercise("matching", "...", "...");
 * }</pre>
 *
 * The static {@link #create} shortcut keeps old callers working.
 */
public class ExerciseFactory {

    private static final ExerciseFactory DEFAULT = withDefaults();

    private final Map<String, ExerciseCreator> creators = new LinkedHashMap<>();

    /** Factory with the three built-in exercise types. */
    public static ExerciseFactory withDefaults() {
        return new ExerciseFactory()
                .register("flashcard", new FlashcardExerciseCreator())
                .register("translation", new TranslationExerciseCreator())
                .register("quiz", new QuizExerciseCreator());
    }

    /** Backward-compatible shortcut that uses the default factory. */
    public static Exercise create(String type, String prompt, String expectedAnswer) {
        return DEFAULT.createExercise(type, prompt, expectedAnswer);
    }

    public ExerciseFactory register(String type, ExerciseCreator creator) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Тип упражнения не может быть пустым");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Creator не может быть null");
        }
        creators.put(normalize(type), creator);
        return this;
    }

    public Exercise createExercise(String type, String prompt, String expectedAnswer) {
        ExerciseCreator creator = type == null ? null : creators.get(normalize(type));
        if (creator == null) {
            throw new IllegalArgumentException("Неизвестный тип упражнения: " + type);
        }
        return creator.create(prompt, expectedAnswer);
    }

    public Set<String> supportedTypes() {
        return Collections.unmodifiableSet(creators.keySet());
    }

    private static String normalize(String type) {
        return type.trim().toLowerCase(Locale.ROOT);
    }
}
