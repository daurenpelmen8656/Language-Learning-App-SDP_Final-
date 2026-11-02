package org.example.strategy;

/**
 * Simplified SM-2 (SuperMemo 2) strategy.
 *
 * Core idea: the interval grows by a factor (easeFactor) after each correct
 * answer. A wrong answer resets the sequence.
 *
 * Simplified rules:
 *   - Base ease factor = 2.5
 *   - After each consecutive correct answer the interval ≈ previous × easeFactor
 *   - Wrong answer → reset to 1 day
 *
 * This is a teaching-friendly simplification; the full SM-2 uses a 0-5
 * quality grade and adjusts the ease factor dynamically.
 */
public class Sm2Strategy implements ReviewStrategy {

    private static final double EASE_FACTOR = 2.5;

    @Override
    public int nextReviewDays(int currentBox, int consecutiveCorrect, boolean wasCorrect) {
        if (!wasCorrect) {
            return 1; // reset
        }

        // First correct answer → 1 day, second → 3 days, then multiply
        switch (consecutiveCorrect) {
            case 0:
            case 1:
                return 1;
            case 2:
                return 3;
            default:
                // interval = previous interval × easeFactor (approximated)
                int interval = (int) Math.round(3 * Math.pow(EASE_FACTOR, consecutiveCorrect - 2));
                return Math.min(interval, 365); // cap at 1 year
        }
    }
}
