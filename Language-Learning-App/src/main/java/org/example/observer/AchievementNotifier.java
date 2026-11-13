package org.example.observer;

/** Tracks a small achievement milestone after ten correct answers. */
public class AchievementNotifier implements ProgressObserver {
    private static final int MILESTONE = 10;
    private int correctAnswers;

    @Override
    public void update(ProgressEvent event) {
        if (!event.isCorrect()) {
            return;
        }

        correctAnswers++;
        if (correctAnswers % MILESTONE == 0) {
        }
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }
}
