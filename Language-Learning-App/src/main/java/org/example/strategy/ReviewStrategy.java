package org.example.strategy;

/**
 * Strategy Pattern — defines how to calculate the next review interval for a word.
 * Different implementations use different spaced-repetition algorithms.
 */
public interface ReviewStrategy {

    /**
     * Calculates after how many days a word should be reviewed again.
     *
     * @param currentBox         the current repetition level (box number, starting from 0)
     * @param consecutiveCorrect how many times the word was answered correctly in a row
     * @param wasCorrect         whether the last answer was correct
     * @return number of days until next review
     */
    int nextReviewDays(int currentBox, int consecutiveCorrect, boolean wasCorrect);
}
