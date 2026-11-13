package org.example.observer;

import java.util.ArrayList;
import java.util.List;

/** Tracks a small achievement milestone after ten correct answers. */
public class AchievementNotifier implements ProgressObserver {
    private static final int MILESTONE = 10;
    private int correctAnswers;
    private final List<String> unlockedAchievements = new ArrayList<>();

    public AchievementNotifier() {
        this(0);
    }

    public AchievementNotifier(int previousCorrectAnswers) {
        if (previousCorrectAnswers < 0) {
            throw new IllegalArgumentException("Correct answer count must be non-negative");
        }
        correctAnswers = previousCorrectAnswers;
        int milestoneCount = previousCorrectAnswers / MILESTONE;
        for (int milestoneIndex = 1; milestoneIndex <= milestoneCount; milestoneIndex++) {
            unlockedAchievements.add((long) milestoneIndex * MILESTONE + " correct answers");
        }
    }

    @Override
    public void update(ProgressEvent event) {
        if (!event.isCorrect()) {
            return;
        }

        correctAnswers++;
        if (correctAnswers % MILESTONE == 0) {
            unlockedAchievements.add(correctAnswers + " correct answers");
        }
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public List<String> getUnlockedAchievements() {
        return List.copyOf(unlockedAchievements);
    }
}
