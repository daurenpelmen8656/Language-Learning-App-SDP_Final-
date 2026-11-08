package org.example.observer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AchievementNotifierTest {
    @Test
    void announcesAchievementAfterTenCorrectAnswers() {
        AchievementNotifier notifier = new AchievementNotifier();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(output));
            for (int i = 0; i < 10; i++) {
                notifier.update(new ProgressEvent("apple", true, 2));
            }
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(10, notifier.getCorrectAnswers());
        assertTrue(output.toString().contains("10 правильных ответов"));
    }
}
