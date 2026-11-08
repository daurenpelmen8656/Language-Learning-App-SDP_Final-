package org.example.observer;

import org.example.singleton.UserSession;

/**
 * Observer that rewards user with XP for correct answers.
 */
public class XpTracker implements ProgressObserver {
    private static final int XP_PER_CORRECT = 10;

    @Override
    public void update(ProgressEvent event) {
        if (event.isCorrect()) {
            UserSession.getInstance().addXp(XP_PER_CORRECT);
            System.out.println("[XP Tracker] +" + XP_PER_CORRECT + " XP! Текущий баланс: "
                    + UserSession.getInstance().getXp() + " XP.");
        }
    }
}
