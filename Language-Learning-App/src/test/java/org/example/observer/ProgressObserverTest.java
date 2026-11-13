package org.example.observer;

import org.example.TestUserProgress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgressObserverTest {
    @Test
    void shouldUpdateProgressObservers_whenPublisherReceivesAnswer() {
        TestUserProgress userProgress = new TestUserProgress();
        ProgressPublisher publisher = new ProgressPublisher();
        publisher.attach(new XpTracker(userProgress));
        publisher.attach(new StreakTracker(userProgress));
        publisher.attach(new ReminderService());

        publisher.notifyObservers(new ProgressEvent("apple", true, 2));

        assertEquals(10, userProgress.getXp());
        assertEquals(1, userProgress.getStreak());
    }
}
