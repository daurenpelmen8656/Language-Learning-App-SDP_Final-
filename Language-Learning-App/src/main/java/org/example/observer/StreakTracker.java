package org.example.observer;

import org.example.singleton.UserProgress;

import java.util.Objects;

/**
 * Observer that tracks the user's streak of correct answers.
 */
public class StreakTracker implements ProgressObserver {
    private final UserProgress userProgress;

    public StreakTracker(UserProgress userProgress) {
        this.userProgress = Objects.requireNonNull(userProgress);
    }

    @Override
    public void update(ProgressEvent event) {
        if (event.isCorrect()) {
            userProgress.incrementStreak();
        } else {
            userProgress.resetStreak();
        }
    }
}
