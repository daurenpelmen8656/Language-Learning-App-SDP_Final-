package org.example.facade;

import org.example.strategy.ReviewStrategy;
import org.example.strategy.LeitnerStrategy;

/**
 * Facade Pattern — provides a simple interface to the whole learning subsystem.
 *
 * Hides Builder, Factory, Strategy, Observer and other internals behind
 * three easy methods: startLesson(), submitAnswer(), showProgress().
 *
 * Right now the methods contain stubs (заглушки) that will be replaced
 * once Builder, Factory, Observer, Adapter, Decorator and Singleton are ready.
 */
public class LearningFacade {

    private final ReviewStrategy reviewStrategy;

    /**
     * Creates a facade with the default Leitner review strategy.
     */
    public LearningFacade() {
        this(new LeitnerStrategy());
    }

    /**
     * Creates a facade with a custom review strategy.
     *
     * @param reviewStrategy the spaced-repetition algorithm to use
     */
    public LearningFacade(ReviewStrategy reviewStrategy) {
        this.reviewStrategy = reviewStrategy;
    }

    // ----------------------------------------------------------------
    //  Public API (stubs — will be wired up during assembly stage)
    // ----------------------------------------------------------------

    /**
     * Starts a new lesson.
     * TODO: use Builder to assemble a Lesson, Factory to create Exercises.
     */
    public void startLesson() {
        System.out.println("[Facade] Starting a new lesson...");
        // TODO: Lesson lesson = new LessonBuilder()...build();
        // TODO: List<Exercise> exercises = ExerciseFactory.create(...);
        System.out.println("[Facade] (stub) Lesson started.");
    }

    /**
     * Submits the user's answer and determines the next review date.
     *
     * @param wordText the text of the word being reviewed
     * @param correct  whether the answer was correct
     */
    public void submitAnswer(String wordText, boolean correct) {
        System.out.println("[Facade] Answer submitted for \"" + wordText + "\" — "
                + (correct ? "CORRECT" : "WRONG"));

        // Stub values — will be loaded from Singleton/UserSession later
        int currentBox = 0;
        int consecutiveCorrect = correct ? 1 : 0;

        int nextDays = reviewStrategy.nextReviewDays(currentBox, consecutiveCorrect, correct);
        System.out.println("[Facade] Next review in " + nextDays + " day(s).");

        // TODO: notify Observer about the answer event
        // TODO: update UserSession (Singleton) with new progress
    }

    /**
     * Shows the current progress of the user.
     * TODO: read data from Singleton (UserSession).
     */
    public void showProgress() {
        System.out.println("[Facade] === User Progress ===");
        // TODO: pull real data from Singleton
        System.out.println("[Facade] (stub) Words learned: 0 | XP: 0 | Streak: 0 days");
        System.out.println("[Facade] ======================");
    }
}
