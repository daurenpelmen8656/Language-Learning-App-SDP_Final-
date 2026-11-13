package org.example.observer;

import org.junit.jupiter.api.Test;
import org.example.singleton.UserSession;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgressObserverTest {
    @Test
    void oneAnswerNotifiesAllSubscribers() {
        ProgressPublisher publisher = new ProgressPublisher();
        publisher.attach(new XpTracker());
        publisher.attach(new StreakTracker());
        publisher.attach(new ReminderService());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(output));
            publisher.notifyObservers(new ProgressEvent("apple", true, 2));
        } finally {
            System.setOut(originalOut);
        }

        String console = output.toString();
        assertTrue(console.contains("[XP Tracker] +10 XP"));
        assertTrue(console.contains("[Streak Tracker]"));
        assertTrue(console.contains("[Reminder Service]"));
        assertTrue(console.contains("apple"));
        assertTrue(console.contains("2"));
    }

    @Test
    void detachedObserverReceivesNoEvent() {
        ProgressPublisher publisher = new ProgressPublisher();
        int[] calls = {0};
        ProgressObserver observer = event -> calls[0]++;
        publisher.attach(observer);
        publisher.detach(observer);
        publisher.notifyObservers(new ProgressEvent("apple", true, 2));
        assertEquals(0, calls[0]);
    }

    @Test
    void incorrectAnswerKeepsXpResetsStreakAndSchedulesReminder() {
        UserSession session = UserSession.getInstance();
        session.incrementStreak();
        int xpBefore = session.getXp();
        ProgressPublisher publisher = new ProgressPublisher();
        publisher.attach(new XpTracker());
        publisher.attach(new StreakTracker());
        publisher.attach(new ReminderService());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            publisher.notifyObservers(new ProgressEvent("apple", false, 1));
        } finally {
            System.setOut(original);
        }
        assertEquals(xpBefore, session.getXp());
        assertEquals(0, session.getStreak());
        assertTrue(output.toString().contains("повторение через 1 дн."));
    }
}
