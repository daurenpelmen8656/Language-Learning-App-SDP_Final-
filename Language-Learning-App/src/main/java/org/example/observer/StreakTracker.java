package org.example.observer;

import org.example.singleton.UserSession;

/**
 * Observer that tracks the user's streak of correct answers.
 */
public class StreakTracker implements ProgressObserver {

    @Override
    public void update(ProgressEvent event) {
        UserSession session = UserSession.getInstance();
        if (event.isCorrect()) {
            session.incrementStreak();
            System.out.println("[Streak Tracker] 🔥 Серия верных ответов: " + session.getStreak() + "!");
        } else {
            session.resetStreak();
            System.out.println("[Streak Tracker] 💔 Серия сброшена.");
        }
    }
}
