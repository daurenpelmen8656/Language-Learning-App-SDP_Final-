package org.example.observer;

import java.util.ArrayList;
import java.util.List;

/** Tracks a small achievement milestone after ten correct answers. */
public class AchievementNotifier implements ProgressObserver {
    private static final int MILESTONE = 10;
    private int correctAnswers;
    private final List<String> unlockedAchievements = new ArrayList<>();

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
