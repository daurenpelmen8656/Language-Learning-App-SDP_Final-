package org.example.facade;

import org.example.TestUserProgress;
import org.example.observer.ProgressPublisher;
import org.example.singleton.WordProgress;
import org.example.strategy.SimpleIntervalStrategy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 13);

    @Test
    void shouldShowOnlyDueWords_whenReviewDateHasArrived() {
        TestUserProgress userProgress = new TestUserProgress();
        userProgress.getProgress("due").setNextReviewDate(TODAY);
        userProgress.getProgress("future").setNextReviewDate(TODAY.plusDays(1));
        ReviewService service = createService(userProgress);

        Map<String, WordProgressView> dueWords = service.getDueWords();

        assertEquals(Map.of("due", new WordProgressView(0, 0, TODAY)), dueWords);
    }

    @Test
    void shouldScheduleNextReviewFromInjectedClock_whenAnswerIsCorrect() {
        TestUserProgress userProgress = new TestUserProgress();
        ReviewService service = createService(userProgress);

        AnswerResult result = service.submitAnswer("vocabulary", true);

        assertEquals(TODAY.plusDays(2), result.nextReviewDate());
        assertTrue(service.getDueWords().isEmpty());
    }

    private ReviewService createService(TestUserProgress userProgress) {
        Clock clock = Clock.fixed(Instant.parse("2026-11-13T00:00:00Z"), ZoneOffset.UTC);
        return new ReviewService(new SimpleIntervalStrategy(), userProgress, new ProgressPublisher(), clock);
    }
}
