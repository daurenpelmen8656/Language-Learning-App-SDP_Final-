package org.example.strategy;

/**
 * Simplest possible strategy with fixed, linearly growing intervals.
 *
 * Each consecutive correct answer adds a fixed step (default 2 days).
 * Wrong answer → review tomorrow.
 *
 * Example progression (correct streak): 2 → 4 → 6 → 8 → …
 */
public class SimpleIntervalStrategy implements ReviewStrategy {

    private static final int STEP_DAYS = 2;

    @Override
    public int nextReviewDays(int currentBox, int consecutiveCorrect, boolean wasCorrect) {
        if (!wasCorrect) {
            return 1;
        }
        return Math.max(1, (consecutiveCorrect + 1) * STEP_DAYS);
    }
}
