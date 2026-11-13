package org.example.observer;

import org.example.singleton.UserProgress;

import java.util.Objects;

/**
 * Observer that rewards user with XP for correct answers.
 */
public class XpTracker implements ProgressObserver {
    private static final int XP_PER_CORRECT = 10;
    private final UserProgress userProgress;

    public XpTracker(UserProgress userProgress) {
        this.userProgress = Objects.requireNonNull(userProgress);
    }

    @Override
    public void update(ProgressEvent event) {
        if (event.isCorrect()) {
            userProgress.addXp(XP_PER_CORRECT);
        }
    }
}
