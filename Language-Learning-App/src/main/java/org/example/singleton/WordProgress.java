package org.example.singleton;

/**
 * Spaced-repetition progress of a single word.
 * The state changes only through {@link #recordAnswer(boolean)},
 * so box and streak cannot get out of sync (no public setters).
 */
public class WordProgress {
    private int box;
    private int consecutiveCorrect;

    public WordProgress() {
        this(0, 0);
    }

    public WordProgress(int box, int consecutiveCorrect) {
        if (box < 0 || consecutiveCorrect < 0) {
            throw new IllegalArgumentException("box и consecutiveCorrect не могут быть отрицательными");
        }
        this.box = box;
        this.consecutiveCorrect = consecutiveCorrect;
    }

    public int getBox() {
        return box;
    }

    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
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
