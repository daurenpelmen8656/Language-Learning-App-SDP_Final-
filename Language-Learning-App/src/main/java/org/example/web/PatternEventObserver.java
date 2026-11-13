package org.example.web;

import org.example.observer.ProgressEvent;
import org.example.observer.ProgressObserver;
import org.example.observer.XpTracker;

import java.util.Objects;
public final class PatternEventObserver implements ProgressObserver {
    private final PatternEventLog eventLog;

    public PatternEventObserver(PatternEventLog eventLog) {
        this.eventLog = Objects.requireNonNull(eventLog);
    }

    @Override
    public void update(ProgressEvent event) {
        if (event.isCorrect()) {
            eventLog.record("Observer", "XpTracker +" + XpTracker.XP_PER_CORRECT + " XP");
            eventLog.record("Observer", "StreakTracker incremented streak");
        } else {
            eventLog.record("Observer", "StreakTracker reset streak");
        }
    }
}
