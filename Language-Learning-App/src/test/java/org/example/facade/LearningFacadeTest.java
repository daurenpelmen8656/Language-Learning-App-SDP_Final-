package org.example.facade;

import org.example.TestUserProgress;
import org.example.adapter.DictionaryAdapter;
import org.example.adapter.ExternalDictionaryApi;
import org.example.observer.ProgressPublisher;
import org.example.observer.StreakTracker;
import org.example.observer.XpTracker;
import org.example.singleton.WordProgress;
import org.example.strategy.LeitnerStrategy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LearningFacadeTest {
    @Test
    void shouldUpdateProgressAndReturnAnswerData_whenAnswerIsSubmitted() {
        TestUserProgress userProgress = new TestUserProgress();
        ProgressPublisher publisher = new ProgressPublisher();
        publisher.attach(new XpTracker(userProgress));
        publisher.attach(new StreakTracker(userProgress));
        LeitnerStrategy strategy = new LeitnerStrategy();
        ReviewService reviewService = new ReviewService(strategy, userProgress, publisher,
                Clock.fixed(Instant.parse("2026-11-13T00:00:00Z"), ZoneOffset.UTC));
        LearningFacade facade = new LearningFacade(new LessonService(
                new DictionaryAdapter(new ExternalDictionaryApi())), reviewService, strategy);

        AnswerResult result = facade.submitAnswer("window", true);

        assertEquals(10, result.xp());
        assertEquals(1, result.streak());
        assertEquals(2, result.nextReviewDays());
        assertEquals("2026-11-15", result.nextReviewDate().toString());
        WordProgress progress = userProgress.getProgress("window");
        assertEquals(1, progress.getBox());
        assertEquals(1, progress.getConsecutiveCorrect());

        facade.submitAnswer("window", false);
        assertEquals(0, userProgress.getStreak());
        assertEquals(0, progress.getBox());
        assertFalse(facade.getDueWords().containsKey("window"));
    }
}
