package org.example.singleton;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Tracks the spaced-repetition progress for a single word.
 */
public class WordProgress {
    private int box;
    private int consecutiveCorrect;
    private LocalDate nextReviewDate = LocalDate.MIN;

    public WordProgress() {
        this(0, 0);
    }

    public WordProgress(int box, int consecutiveCorrect) {
        if (box < 0 || consecutiveCorrect < 0) {
            throw new IllegalArgumentException("Progress values must not be negative");
        }
        this.box = box;
        this.consecutiveCorrect = consecutiveCorrect;
    }

    public WordProgress(int box, int consecutiveCorrect, LocalDate nextReviewDate) {
        if (box < 0 || consecutiveCorrect < 0) {
            throw new IllegalArgumentException("Progress values must not be negative");
        }
        this.box = box;
        this.consecutiveCorrect = consecutiveCorrect;
        this.nextReviewDate = Objects.requireNonNull(nextReviewDate);
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
    }

    public LocalDate getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(LocalDate nextReviewDate) {
        this.nextReviewDate = Objects.requireNonNull(nextReviewDate);
    }

    public void setConsecutiveCorrect(int consecutiveCorrect) {
        this.consecutiveCorrect = consecutiveCorrect;
    }

    public void recordAnswer(boolean correct) {
        if (correct) {
            box++;
            consecutiveCorrect++;
        } else {
            box = 0;
            consecutiveCorrect = 0;
        }
    }

    @Override
    public String toString() {
        return "Box: " + box + ", Streak: " + consecutiveCorrect;
    }
}
