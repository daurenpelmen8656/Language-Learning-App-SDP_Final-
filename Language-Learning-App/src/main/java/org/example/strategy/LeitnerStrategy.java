package org.example.strategy;

/**
 * Leitner System strategy.
 *
 * Words move between "boxes":
 *   - Correct answer → move to the next box (longer interval).
 *   - Wrong answer   → move back to box 0 (review tomorrow).
 *
 * Box intervals: 1, 2, 4, 8, 16 days (boxes 0–4).
 * After box 4 the word is considered "learned" and stays at 30 days.
 */
public class LeitnerStrategy implements ReviewStrategy {

    private static final int[] BOX_INTERVALS = {1, 2, 4, 8, 16, 30};

    @Override
    public int nextReviewDays(int currentBox, int consecutiveCorrect, boolean wasCorrect) {
        if (!wasCorrect) {
            // Wrong answer — go back to box 0 → review in 1 day
            return BOX_INTERVALS[0];
        }

        // Correct answer — promote to next box
        int nextBox = Math.min(currentBox + 1, BOX_INTERVALS.length - 1);
        return BOX_INTERVALS[nextBox];
    }
}
