package org.example;

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
import org.example.singleton.UserProgress;
import org.example.singleton.UserSession;
import org.example.strategy.LeitnerStrategy;
import org.example.strategy.ReviewStrategy;
import org.example.strategy.SimpleIntervalStrategy;
import org.example.strategy.Sm2Strategy;
import org.example.ui.ConsoleUi;

import java.time.Clock;
import java.util.Map;

/** Builds the application object graph. */
public final class AppConfig {
    private AppConfig() { }

    public static ConsoleUi createConsoleUi() {
        UserProgress userProgress = UserSession.getInstance();
        DictionaryService dictionaryService = new DictionaryAdapter(new ExternalDictionaryApi());
        ProgressPublisher publisher = new ProgressPublisher();
        publisher.attach(new XpTracker(userProgress));
        publisher.attach(new StreakTracker(userProgress));
        publisher.attach(new ReminderService());
        publisher.attach(new AchievementNotifier());
        LeitnerStrategy strategy = new LeitnerStrategy();
        ReviewService reviewService = new ReviewService(strategy, userProgress, publisher, Clock.systemDefaultZone());
        LearningFacade facade = new LearningFacade(new LessonService(dictionaryService), reviewService, strategy);
        Map<String, ReviewStrategy> availableStrategies = Map.of(
                ConsoleUi.LEITNER_CHOICE, strategy,
                ConsoleUi.SM2_CHOICE, new Sm2Strategy(),
                ConsoleUi.SIMPLE_INTERVAL_CHOICE, new SimpleIntervalStrategy());
        return new ConsoleUi(facade, availableStrategies);
    }
}
