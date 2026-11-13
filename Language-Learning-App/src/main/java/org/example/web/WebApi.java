package org.example.web;

import org.example.facade.AnswerResult;
import org.example.facade.LearningFacade;
import org.example.facade.LessonService;
import org.example.facade.LessonView;
import org.example.facade.ProgressView;
import org.example.facade.ReviewService;
import org.example.facade.WordProgressView;
import org.example.facade.WordView;
import org.example.factory.Exercise;
import org.example.observer.AchievementNotifier;
import org.example.singleton.SessionStorage;
import org.example.singleton.UserSession;
import org.example.strategy.ReviewStrategy;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WebApi {
    static final String LEITNER_STRATEGY = "leitner";
    static final String SM2_STRATEGY = "sm2";
    static final String SIMPLE_STRATEGY = "simple";
    private static final String BUILDER_PATTERN = "Builder";
    private static final String FACTORY_PATTERN = "Factory";
    private static final String ADAPTER_PATTERN = "Adapter";
    private static final String DECORATOR_PATTERN = "Decorator";
    private static final String FACADE_PATTERN = "Facade";
    private static final String SINGLETON_PATTERN = "Singleton";
    private static final String NEXT_REVIEW_EVENT = "next review in %d days";
    private static final int MAX_DICTIONARY_EVENT_SAMPLES = 5;
    private final LessonService lessonService;
    private final ReviewService reviewService;
    private final LearningFacade learningFacade;
    private final UserSession userSession;
    private final SessionStorage sessionStorage;
    private final AchievementNotifier achievementNotifier;
    private final PatternEventLog eventLog;
    private final Map<String, ReviewStrategy> strategies;

    public WebApi(LessonService lessonService, ReviewService reviewService, LearningFacade learningFacade,
                  UserSession userSession, SessionStorage sessionStorage,
                  AchievementNotifier achievementNotifier, PatternEventLog eventLog,
                  Map<String, ReviewStrategy> strategies) {
        this.lessonService = Objects.requireNonNull(lessonService);
        this.reviewService = Objects.requireNonNull(reviewService);
        this.learningFacade = Objects.requireNonNull(learningFacade);
        this.userSession = Objects.requireNonNull(userSession);
        this.sessionStorage = Objects.requireNonNull(sessionStorage);
        this.achievementNotifier = Objects.requireNonNull(achievementNotifier);
        this.eventLog = Objects.requireNonNull(eventLog);
        this.strategies = Map.copyOf(strategies);
    }

    public Map<String, Object> getLesson() {
        LessonView lesson = lessonService.buildLesson();
        recordLessonEvents(lesson);
        return lessonData(lesson);
    }

    public Map<String, Object> submitAnswer(String word, boolean correct) throws IOException {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word must not be blank");
        }
        AnswerResult result = reviewService.submitAnswer(word, correct);
        sessionStorage.save(userSession);
        eventLog.record(SINGLETON_PATTERN, "UserSession saved");
        String strategyName = strategyName(learningFacade.getReviewStrategy());
        eventLog.record("Strategy[" + strategyName + "]",
                NEXT_REVIEW_EVENT.formatted(result.nextReviewDays()));

        ProgressView progress = reviewService.getProgress(strategyName);
        Map<String, Object> answer = new LinkedHashMap<>();
        answer.put("word", result.wordText());
        answer.put("correct", result.correct());
        answer.put("nextReviewDays", result.nextReviewDays());
        answer.put("nextReviewDate", result.nextReviewDate().toString());
        answer.put("xp", result.xp());
        answer.put("streak", result.streak());
        answer.put("wordsLearned", progress.wordsLearned());
        answer.put("unlockedAchievements", achievementNotifier.getUnlockedAchievements());
        return answer;
    }

    public Map<String, Object> getProgress() {
        ProgressView progress = reviewService.getProgress(strategyName(learningFacade.getReviewStrategy()));
        Map<String, Object> wordData = new LinkedHashMap<>();
        progress.words().forEach((word, details) -> wordData.put(word, wordProgressData(details)));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("username", progress.username());
        result.put("xp", progress.xp());
        result.put("streak", progress.streak());
        result.put("wordsLearned", progress.wordsLearned());
        result.put("dueWords", reviewService.getDueWords().size());
        result.put("strategy", strategyName(learningFacade.getReviewStrategy()));
        result.put("words", wordData);
        result.put("unlockedAchievements", achievementNotifier.getUnlockedAchievements());
        eventLog.record(SINGLETON_PATTERN, "UserSession progress read");
        return result;
    }

    public Map<String, Object> switchStrategy(String name) {
        if (name == null || !strategies.containsKey(name)) {
            throw new IllegalArgumentException("Unknown strategy. Use leitner, sm2, or simple.");
        }
        ReviewStrategy strategy = strategies.get(name);
        learningFacade.setReviewStrategy(strategy);
        String normalizedName = strategyName(strategy);
        eventLog.record("Strategy[" + normalizedName + "]", "strategy selected");
        return Map.of("name", name, "strategy", normalizedName);
    }

    public List<Map<String, Object>> getEvents() {
        return eventLog.latest().stream().map(event -> Map.<String, Object>of(
                "pattern", event.pattern(), "message", event.message(), "occurredAt", event.occurredAt().toString()))
                .toList();
    }

    private Map<String, Object> lessonData(LessonView lesson) {
        List<Map<String, Object>> words = lesson.words().stream().map(this::wordData).toList();
        List<Map<String, Object>> cards = lesson.cards().stream().map(card -> Map.<String, Object>of(
                "word", card.wordText(), "decoration", card.decoration(), "content", card.content(),
                "renderedText", card.renderedText())).toList();
        List<Map<String, Object>> exercises = java.util.stream.IntStream.range(0, lesson.exercises().size())
                .mapToObj(index -> exerciseData(lesson.exercises().get(index),
                        lesson.words().get(index / LessonService.EXERCISES_PER_WORD)))
                .toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title", lesson.title());
        result.put("level", lesson.level());
        result.put("words", words);
        result.put("cards", cards);
        result.put("exercises", exercises);
        return result;
    }

    private Map<String, Object> wordData(WordView word) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("text", word.text());
        result.put("translation", word.translation());
        result.put("kazakhTranslation", word.kazakhTranslation());
        result.put("level", word.level() == null ? "" : word.level());
        return result;
    }

    private Map<String, Object> exerciseData(Exercise exercise, WordView word) {
        return Map.of("type", exercise.getType(), "question", exercise.getQuestion(),
                "options", exercise.getOptions(), "correctAnswer", exercise.getCorrectAnswer(), "word", word.text());
    }

    private Map<String, Object> wordProgressData(WordProgressView details) {
        return Map.of("box", details.box(), "consecutiveCorrect", details.consecutiveCorrect(),
                "nextReviewDate", details.nextReviewDate().toString());
    }

    private void recordLessonEvents(LessonView lesson) {
        eventLog.record(BUILDER_PATTERN, "lesson built (" + lesson.words().size() + " words)");
        lesson.exercises().stream()
                .map(exercise -> exercise.getClass().getSimpleName())
                .distinct()
                .forEach(exerciseType -> eventLog.record(FACTORY_PATTERN, "created " + exerciseType));
        lesson.words().stream().limit(MAX_DICTIONARY_EVENT_SAMPLES).forEach(word ->
                eventLog.record(ADAPTER_PATTERN,
                        "entry " + word.text() + "|" + word.translation() + " -> Word"));
        eventLog.record(ADAPTER_PATTERN, lesson.words().size() + " dictionary entries loaded");
        for (var card : lesson.cards()) {
            eventLog.record(DECORATOR_PATTERN, "card + " + card.decoration());
        }
        eventLog.record(FACADE_PATTERN, "lesson data returned");
    }

    private String strategyName(ReviewStrategy strategy) {
        for (Map.Entry<String, ReviewStrategy> entry : strategies.entrySet()) {
            if (entry.getValue().getClass().equals(strategy.getClass())) {
                return switch (entry.getKey()) {
                    case LEITNER_STRATEGY -> "Leitner";
                    case SM2_STRATEGY -> "Sm2";
                    default -> "SimpleInterval";
                };
            }
        }
        return strategy.getClass().getSimpleName();
    }
}
