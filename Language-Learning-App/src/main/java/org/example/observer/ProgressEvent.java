package org.example.observer;

/**
 * Event object carrying information about user's answer and review calculation.
 */
public class ProgressEvent {
    private final String wordText;
    private final boolean correct;
    private final int nextReviewDays;

    public ProgressEvent(String wordText, boolean correct, int nextReviewDays) {
        this.wordText = wordText;
        this.correct = correct;
        this.nextReviewDays = nextReviewDays;
    }

    public String getWordText() {
        return wordText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getNextReviewDays() {
        return nextReviewDays;
    }
}
