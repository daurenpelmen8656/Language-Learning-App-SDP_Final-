package org.example.facade;

import org.example.observer.ProgressEvent;
import org.example.observer.ProgressPublisher;
import org.example.singleton.UserSession;
import org.example.singleton.WordProgress;
import org.example.strategy.ReviewStrategy;

/** Coordinates review calculation, progress update, and event publication. */
public class ReviewService {
    private final ProgressPublisher publisher;

    public ReviewService(ProgressPublisher publisher) {
        this.publisher = publisher;
    }

    public void submitAnswer(String wordText, boolean correct, ReviewStrategy strategy) {
        UserSession session = UserSession.getInstance();
        WordProgress progress = session.getProgress(wordText);
        int nextDays = strategy.nextReviewDays(progress.getBox(), progress.getConsecutiveCorrect(), correct);
        boolean firstLearned = progress.getBox() == 0 && correct;
        progress.recordAnswer(correct);
        if (firstLearned) session.incrementWordsLearned();
        publisher.notifyObservers(new ProgressEvent(wordText, correct, nextDays));
    }
}
