package org.example.observer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementNotifierTest {
    @Test
    void shouldTrackAchievementMilestone_whenTenCorrectAnswersArrive() {
        AchievementNotifier notifier = new AchievementNotifier();
        for (int answerNumber = 0; answerNumber < 10; answerNumber++) {
            notifier.update(new ProgressEvent("apple", true, 2));
        }

        assertEquals(10, notifier.getCorrectAnswers());
    }
}
