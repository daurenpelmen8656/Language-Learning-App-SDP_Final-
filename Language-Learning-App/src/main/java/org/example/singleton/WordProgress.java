package org.example.singleton;

/**
 * Tracks the spaced-repetition progress for a single word.
 */
public class WordProgress {
    private int box;
    private int consecutiveCorrect;

    public WordProgress() {
        this.box = 0;
        this.consecutiveCorrect = 0;
    }

    public WordProgress(int box, int consecutiveCorrect) {
        this.box = box;
        this.consecutiveCorrect = consecutiveCorrect;
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
