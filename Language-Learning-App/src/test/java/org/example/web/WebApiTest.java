package org.example.web;

import org.example.adapter.DictionaryAdapter;
import org.example.adapter.ExternalDictionaryApi;
import org.example.facade.LearningFacade;
import org.example.facade.LessonService;
import org.example.facade.ReviewService;
import org.example.observer.AchievementNotifier;
import org.example.observer.ProgressPublisher;
import org.example.observer.ReminderService;
import org.example.observer.StreakTracker;
import org.example.observer.XpTracker;
import org.example.singleton.SessionStorage;
import org.example.singleton.UserProgress;
import org.example.singleton.UserSession;
import org.example.strategy.LeitnerStrategy;
import org.example.strategy.ReviewStrategy;
import org.example.strategy.SimpleIntervalStrategy;
import org.example.strategy.Sm2Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebApiTest {
    @TempDir
    Path temporaryDirectory;

    @BeforeEach
    void resetSingleton() {
        UserSession.resetForTests();
    }

    @Test
    void shouldEscapeAndParseJsonStrings_whenValuesContainControlCharacters() {
        String encoded = JsonCodec.stringify(Map.of("text", "quote: \" slash: \\ line:\n Kazakh: ә"));

        assertEquals("quote: \" slash: \\ line:\n Kazakh: ә", JsonCodec.parseObject(encoded).get("text"));
    }

    @Test
    void shouldSwitchReviewStrategy_whenStrategyNameIsSupported() throws Exception {
        WebApi api = createApi();

        Map<String, Object> response = api.switchStrategy("sm2");
        Map<String, Object> answer = api.submitAnswer("apple", true);

        assertEquals("Sm2", response.get("strategy"));
        assertEquals(1, ((Number) answer.get("nextReviewDays")).intValue());
        assertTrue(java.nio.file.Files.exists(temporaryDirectory.resolve("progress.properties")));
    }

    @Test
    void shouldKeepOnlyLatestThirtyPatternEvents_whenLogReceivesMore() {
        PatternEventLog eventLog = new PatternEventLog(Clock.fixed(
                Instant.parse("2026-11-13T00:00:00Z"), ZoneOffset.UTC));
        for (int eventNumber = 0; eventNumber < 31; eventNumber++) {
            eventLog.record("Builder", "event " + eventNumber);
        }

        assertEquals(30, eventLog.latest().size());
        assertEquals("event 30", eventLog.latest().getFirst().message());
    }

    private WebApi createApi() throws Exception {
        UserSession session = UserSession.getInstance();
        UserProgress userProgress = session;
        AchievementNotifier achievementNotifier = new AchievementNotifier();
        ProgressPublisher publisher = new ProgressPublisher();
        PatternEventLog eventLog = new PatternEventLog();
        publisher.attach(new XpTracker(userProgress));
        publisher.attach(new StreakTracker(userProgress));
        publisher.attach(new ReminderService());
        publisher.attach(achievementNotifier);
        publisher.attach(new PatternEventObserver(eventLog));
        ReviewStrategy leitner = new LeitnerStrategy();
        ReviewService reviewService = new ReviewService(leitner, userProgress, publisher,
                Clock.fixed(Instant.parse("2026-11-13T00:00:00Z"), ZoneOffset.UTC));
        LessonService lessonService = new LessonService(new DictionaryAdapter(new ExternalDictionaryApi()));
        LearningFacade facade = new LearningFacade(lessonService, reviewService, leitner);
        Map<String, ReviewStrategy> strategies = Map.of(
                "leitner", leitner,
                "sm2", new Sm2Strategy(),
                "simple", new SimpleIntervalStrategy());
        return new WebApi(lessonService, reviewService, facade, session,
                new SessionStorage(temporaryDirectory.resolve("progress.properties")), achievementNotifier,
                eventLog, strategies);
    }
}
