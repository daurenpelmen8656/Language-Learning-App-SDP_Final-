package org.example.observer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
