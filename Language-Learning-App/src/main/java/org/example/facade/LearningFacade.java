package org.example.facade;

import org.example.strategy.ReviewStrategy;

import java.util.Map;
import java.util.Objects;

public final class LearningFacade {
    private final LessonService lessonService;
    private final ReviewService reviewService;
    private ReviewStrategy reviewStrategy;

    public LearningFacade(LessonService lessonService, ReviewService reviewService, ReviewStrategy reviewStrategy) {
        this.lessonService = Objects.requireNonNull(lessonService);
        this.reviewService = Objects.requireNonNull(reviewService);
        this.reviewStrategy = Objects.requireNonNull(reviewStrategy);
    }

    public LessonView buildLesson() {
        return lessonService.buildLesson();
    }

    public WordView findWord(String key) {
        return lessonService.findWord(key);
    }

    public AnswerResult submitAnswer(String wordText, boolean correct) {
        return reviewService.submitAnswer(wordText, correct);
    }

    public Map<String, WordProgressView> getDueWords() {
        return reviewService.getDueWords();
    }

    public ProgressView getProgress() {
        return reviewService.getProgress(reviewStrategy.getClass().getSimpleName());
    }

    public ReviewStrategy getReviewStrategy() {
        return reviewStrategy;
    }

    public void setReviewStrategy(ReviewStrategy reviewStrategy) {
        this.reviewStrategy = Objects.requireNonNull(reviewStrategy);
        reviewService.setReviewStrategy(reviewStrategy);
    }
}
