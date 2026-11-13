package org.example.web;

import org.example.adapter.DictionaryAdapter;
import org.example.adapter.DictionaryService;
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

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public final class WebMain {
    public static final int PORT = 8080;

    private WebMain() { }

    public static void main(String[] args) throws IOException {
        createServer(PORT).start();
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public static WebServer createServer(int port) throws IOException {
        UserSession userSession = UserSession.getInstance();
        UserProgress userProgress = userSession;
        PatternEventLog eventLog = new PatternEventLog();
        SessionStorage sessionStorage = new SessionStorage();
        boolean loaded = sessionStorage.load(userSession);
        eventLog.record("Singleton", loaded ? "UserSession loaded" : "UserSession initialized");

        DictionaryService dictionaryService = new DictionaryAdapter(new ExternalDictionaryApi());
        ProgressPublisher publisher = new ProgressPublisher();
        XpTracker xpTracker = new XpTracker(userProgress);
        StreakTracker streakTracker = new StreakTracker(userProgress);
        ReminderService reminderService = new ReminderService();
        AchievementNotifier achievementNotifier = new AchievementNotifier(
                userProgress.getXp() / XpTracker.XP_PER_CORRECT);
        publisher.attach(xpTracker);
        publisher.attach(streakTracker);
        publisher.attach(reminderService);
        publisher.attach(achievementNotifier);
        publisher.attach(new PatternEventObserver(eventLog));

        LeitnerStrategy leitnerStrategy = new LeitnerStrategy();
        ReviewService reviewService = new ReviewService(leitnerStrategy, userProgress, publisher, Clock.systemDefaultZone());
        LessonService lessonService = new LessonService(dictionaryService);
        LearningFacade facade = new LearningFacade(lessonService, reviewService, leitnerStrategy);
        Map<String, ReviewStrategy> strategies = Map.of(
                WebApi.LEITNER_STRATEGY, leitnerStrategy,
                WebApi.SM2_STRATEGY, new Sm2Strategy(),
                WebApi.SIMPLE_STRATEGY, new SimpleIntervalStrategy());
        WebApi api = new WebApi(lessonService, reviewService, facade, userSession,
                sessionStorage, achievementNotifier, eventLog, strategies);
        return new WebServer(api, port);
    }
}
