package org.example.facade;

import org.example.observer.ProgressEvent;
import org.example.observer.ProgressPublisher;
import org.example.singleton.UserProgress;
import org.example.singleton.WordProgress;
import org.example.strategy.ReviewStrategy;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ReviewService {
    private ReviewStrategy reviewStrategy;
    private final UserProgress userProgress;
    private final ProgressPublisher publisher;
    private final Clock clock;

    public ReviewService(ReviewStrategy reviewStrategy, UserProgress userProgress,
                         ProgressPublisher publisher, Clock clock) {
        this.reviewStrategy = Objects.requireNonNull(reviewStrategy);
        this.userProgress = Objects.requireNonNull(userProgress);
        this.publisher = Objects.requireNonNull(publisher);
        this.clock = Objects.requireNonNull(clock);
    }

    public AnswerResult submitAnswer(String wordText, boolean correct) {
        WordProgress progress = userProgress.getProgress(wordText);
        int days = reviewStrategy.nextReviewDays(progress.getBox(), progress.getConsecutiveCorrect(), correct);
        boolean newlyLearned = progress.getBox() == 0 && correct;
        progress.recordAnswer(correct);
        LocalDate nextReviewDate = LocalDate.now(clock).plusDays(days);
        progress.setNextReviewDate(nextReviewDate);
        if (newlyLearned) {
            userProgress.incrementWordsLearned();
        }
        publisher.notifyObservers(new ProgressEvent(wordText, correct, days));
        return new AnswerResult(wordText, correct, days, nextReviewDate, userProgress.getXp(), userProgress.getStreak());
    }

    public void setReviewStrategy(ReviewStrategy reviewStrategy) {
        this.reviewStrategy = Objects.requireNonNull(reviewStrategy);
    }

    public Map<String, WordProgressView> getDueWords() {
        return userProgress.getDueProgress(LocalDate.now(clock)).entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> {
                    WordProgress progress = entry.getValue();
                    return new WordProgressView(progress.getBox(), progress.getConsecutiveCorrect(),
                            progress.getNextReviewDate());
                }));
    }

    public ProgressView getProgress(String strategyName) {
        Map<String, WordProgressView> words = userProgress.getAllProgress().entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> {
                    WordProgress progress = entry.getValue();
                    return new WordProgressView(progress.getBox(), progress.getConsecutiveCorrect(),
                            progress.getNextReviewDate());
                }));
        return new ProgressView(userProgress.getUsername(), userProgress.getXp(), userProgress.getStreak(),
                userProgress.getWordsLearned(), strategyName, words);
    }
}
